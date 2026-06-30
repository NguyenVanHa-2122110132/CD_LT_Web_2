package com.example.lt_web2.service;

import com.example.lt_web2.entity.*;
import com.example.lt_web2.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AiService {

    @Autowired
    private GroqService groqService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private ChatConversationRepository chatConversationRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Giờ làm việc cửa hàng: 08:00 - 23:00. Ngoài khung này (23:01 - 07:59) AI tự
    // động trực ban.
    private boolean isOutsideWorkingHours() {
        java.time.LocalTime now = java.time.LocalTime.now();
        java.time.LocalTime closeTime = java.time.LocalTime.of(23, 1);
        java.time.LocalTime openTime = java.time.LocalTime.of(7, 59);
        return now.isAfter(closeTime) || now.isBefore(openTime);
    }

    // ====== FR-AI-003 + FR-AI-004: Nhận tin nhắn khách, phân loại ý định + tự trả
    // lời ngoài giờ ======
    @org.springframework.transaction.annotation.Transactional
    public com.example.lt_web2.dto.ChatResponse handleIncomingMessage(com.example.lt_web2.dto.ChatRequest req) {

        // BƯỚC 1: Lấy hoặc tạo conversation
        ChatConversation conversation;
        if (req.getConversationId() != null) {
            conversation = chatConversationRepository.findById(req.getConversationId())
                    .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                            org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy hội thoại"));
        } else {
            conversation = new ChatConversation();
            conversation.setStatus("OPEN");
            if (req.getCustomerId() != null) {
                Customer c = new Customer();
                c.setId(req.getCustomerId());
                conversation.setCustomer(c);
            }
            conversation = chatConversationRepository.save(conversation);
        }

        // Lưu tin nhắn của khách
        ChatMessage customerMsg = new ChatMessage();
        customerMsg.setConversation(conversation);
        customerMsg.setSenderType("CUSTOMER");
        customerMsg.setMessage(req.getMessage());
        chatMessageRepository.save(customerMsg);

        // BƯỚC 2 (FR-AI-004): Phân loại ý định (Intent) bằng AI
        String classifyPrompt = """
                Bạn là NLP Classifier phân loại ý định khách hàng nhắn tin cho shop thời trang.
                Đọc tin nhắn và trả về DUY NHẤT 1 trong các giá trị (không thêm chữ nào khác):
                WARM_LEAD - nếu khách hỏi về giá ship, phí vận chuyển
                HOT_LEAD - nếu khách cung cấp địa chỉ, số điện thoại, hoặc chốt mua hàng
                COMPLAINT - nếu khách chê chất lượng vải/sản phẩm, đòi trả hàng/hoàn tiền
                NONE - nếu không thuộc 3 loại trên
                """;
        String tagResult = groqService.chat(classifyPrompt, req.getMessage()).trim().toUpperCase();

        String finalTag = null;
        if (tagResult.contains("HOT_LEAD")) {
            finalTag = "HOT_LEAD";
            conversation.setPriority(2); // đẩy cuộc chat lên hàng đầu
        } else if (tagResult.contains("COMPLAINT")) {
            finalTag = "COMPLAINT";
            conversation.setPriority(3); // khiếu nại ưu tiên cao nhất
            // TODO: bắn thông báo cho Quản lý cửa hàng (tích hợp NotificationRepository khi
            // cần)
        } else if (tagResult.contains("WARM_LEAD")) {
            finalTag = "WARM_LEAD";
            conversation.setPriority(1);
        }

        if (finalTag != null) {
            conversation.setTag(finalTag);
        }
        chatConversationRepository.save(conversation);

        // BƯỚC 3 (FR-AI-003): Nếu ngoài giờ làm việc, AI tự động trả lời thông tin tĩnh
        String aiReply = null;
        if (isOutsideWorkingHours()) {
            String staticInfoPrompt = """
                    Bạn là AI trực ban của shop thời trang, hoạt động ngoài giờ làm việc (23:01 - 07:59).
                    Trả lời khách dựa trên các thông tin tĩnh sau:
                    - Địa chỉ shop: 123 Lê Lợi, Quận 1, TP.HCM
                    - Giờ mở cửa: 08:00 - 23:00 hàng ngày
                    - Chính sách đổi trả: trong vòng 7 ngày kể từ ngày mua, hàng còn tem mác, chưa qua sử dụng
                    - Nếu khách hỏi giá sản phẩm cụ thể mà không có trong thông tin trên, xin lỗi và hẹn nhân viên tư vấn vào giờ làm việc.
                    Trả lời ngắn gọn, thân thiện, xưng "em" gọi khách "mình"/"shop".
                    """;
            aiReply = groqService.chat(staticInfoPrompt, req.getMessage());

            ChatMessage aiMsg = new ChatMessage();
            aiMsg.setConversation(conversation);
            aiMsg.setSenderType("AI");
            aiMsg.setMessage(aiReply);
            chatMessageRepository.save(aiMsg);
        }

        return new com.example.lt_web2.dto.ChatResponse(conversation.getId(), aiReply, finalTag);
    }

    // ====== FR-AI-001: Chatbot tư vấn sản phẩm (RAG) ======
    public String answerProductInquiry(String customerMessage) {

        // BƯỚC 1: Dùng AI bóc tách thực thể từ câu hỏi tự do của khách
        String extractPrompt = """
                Bạn là module NLP bóc tách thực thể cho shop thời trang.
                Đọc câu hỏi của khách và trả về DUY NHẤT 1 JSON (không thêm chữ nào khác) theo format:
                {"productCode": "...", "size": "...", "color": "..."}
                Nếu không tìm thấy thông tin nào, để giá trị là null.
                Mã sản phẩm thường viết hoa kiểu V01, AO01... Size thường là S/M/L/XL hoặc số.
                """;

        String entityJson = groqService.chat(extractPrompt, customerMessage);
        entityJson = cleanJsonResponse(entityJson);

        String tempProductCode = null, tempSize = null, tempColor = null;
        try {
            JsonNode node = objectMapper.readTree(entityJson);
            tempProductCode = getTextOrNull(node, "productCode");
            tempSize = getTextOrNull(node, "size");
            tempColor = getTextOrNull(node, "color");
        } catch (Exception e) {
            return "Dạ shop chưa hiểu rõ câu hỏi của mình, mình có thể cho shop biết mã sản phẩm hoặc tên sản phẩm cụ thể được không ạ?";
        }

        final String productCode = tempProductCode;
        final String size = tempSize;
        final String color = tempColor;

        if (productCode == null) {
            return "Dạ mình cho shop xin mã sản phẩm hoặc tên sản phẩm cụ thể để shop kiểm tra tồn kho giúp mình nhé ạ!";
        }

        // BƯỚC 2: Query thật vào DB tồn kho (RAG - lấy dữ liệu thật, không để AI tự
        // đoán)
        Optional<Product> productOpt = productRepository.findAll().stream()
                .filter(p -> p.getProductCode() != null && p.getProductCode().equalsIgnoreCase(productCode))
                .findFirst();

        if (productOpt.isEmpty()) {
            return "Dạ shop không tìm thấy sản phẩm với mã \"" + productCode
                    + "\", mình kiểm tra lại giúp shop mã sản phẩm nhé ạ!";
        }

        Product product = productOpt.get();
        List<ProductVariant> variants = productVariantRepository.findByProductId(product.getId());

        // Lọc theo size/color nếu khách có nói rõ
        Optional<ProductVariant> matchedVariant = variants.stream()
                .filter(v -> (size == null || v.getSize().equalsIgnoreCase(size)))
                .filter(v -> (color == null || v.getColor().equalsIgnoreCase(color)))
                .findFirst();

        // BƯỚC 3: Đưa dữ liệu thật cho AI soạn câu trả lời tự nhiên
        String contextData;
        if (matchedVariant.isPresent()) {
            ProductVariant v = matchedVariant.get();
            contextData = String.format(
                    "Sản phẩm: %s, mã %s, màu %s, size %s, giá bán: %,.0f đ, số lượng tồn: %d",
                    product.getName(), product.getProductCode(), v.getColor(), v.getSize(),
                    v.getSalePrice(), v.getStockQuantity());
        } else {
            contextData = "Không tìm thấy đúng biến thể màu/size khách yêu cầu trong sản phẩm \""
                    + product.getName() + "\". Các biến thể hiện có: " +
                    variants.stream()
                            .map(v -> v.getColor() + " - size " + v.getSize() + " (còn " + v.getStockQuantity() + ")")
                            .reduce((a, b) -> a + "; " + b).orElse("không còn biến thể nào");
        }

        String replyPrompt = """
                Bạn là nhân viên tư vấn bán hàng shop thời trang, xưng "em", gọi khách là "mình"/"shop".
                Dựa CHÍNH XÁC vào dữ liệu được cung cấp (không tự bịa thêm thông tin, không tự đoán giá hoặc tồn kho khác).
                Nếu còn hàng: xác nhận còn hàng, báo giá, hỏi khách có muốn lên đơn ship không.
                Nếu hết hàng hoặc không đúng size/màu: báo khách biết và gợi ý các lựa chọn khác đang có (nếu có dữ liệu).
                Trả lời ngắn gọn 1-2 câu, giọng thân thiện, đúng văn hoá bán hàng online Việt Nam.
                """;

        return groqService.chat(replyPrompt, "Dữ liệu tồn kho: " + contextData);
    }

    // ====== FR-AI-002: Gợi ý phối đồ ======
    public List<com.example.lt_web2.dto.StyleSuggestionResponse> suggestStyle(Integer variantId) {

        ProductVariant baseVariant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));

        Product baseProduct = baseVariant.getProduct();

        // Lấy danh sách các sản phẩm CÒN HÀNG khác (loại trừ chính sản phẩm đang quét)
        // để AI chọn lọc
        List<ProductVariant> candidates = productVariantRepository.findAll().stream()
                .filter(v -> v.getStockQuantity() != null && v.getStockQuantity() > 0)
                .filter(v -> !v.getId().equals(baseVariant.getId()))
                .filter(v -> v.getProduct() != null && !v.getProduct().getId().equals(baseProduct.getId()))
                .toList();

        if (candidates.isEmpty()) {
            return List.of();
        }

        StringBuilder candidateListText = new StringBuilder();
        for (ProductVariant v : candidates) {
            candidateListText.append(String.format("- SKU: %s | Tên: %s | Màu: %s | Danh mục: %s%n",
                    v.getSkuCode(), v.getProduct().getName(), v.getColor(),
                    v.getProduct().getCategory() != null ? v.getProduct().getCategory().getName() : "N/A"));
        }

        String prompt = """
                Bạn là AI Stylist tư vấn phối đồ cho shop thời trang.
                Sản phẩm khách đang chọn: "%s" màu %s.
                Dưới đây là danh sách các sản phẩm ĐANG CÒN HÀNG khác trong shop:
                %s
                Hãy chọn ĐÚNG 2-3 sản phẩm trong danh sách trên (chỉ chọn SKU có trong danh sách, không tự bịa thêm)
                phù hợp phối cùng theo quy tắc phối màu thời trang cơ bản (tương phản hoặc tông cùng màu, cân đối phong cách).
                Trả về DUY NHẤT JSON dạng array, không thêm chữ nào khác:
                [{"skuCode": "...", "reason": "lý do ngắn 1 câu vì sao hợp"}]
                """
                .formatted(baseProduct.getName(), baseVariant.getColor(), candidateListText);

        String aiResponse = groqService.chat("Bạn là AI Stylist chuyên nghiệp.", prompt);
        aiResponse = cleanJsonResponse(aiResponse);

        List<com.example.lt_web2.dto.StyleSuggestionResponse> result = new java.util.ArrayList<>();
        try {
            JsonNode arrayNode = objectMapper.readTree(aiResponse);
            for (JsonNode item : arrayNode) {
                String sku = getTextOrNull(item, "skuCode");
                String reason = getTextOrNull(item, "reason");
                if (sku == null)
                    continue;

                candidates.stream()
                        .filter(v -> v.getSkuCode().equalsIgnoreCase(sku))
                        .findFirst()
                        .ifPresent(v -> result.add(new com.example.lt_web2.dto.StyleSuggestionResponse(
                                v.getSkuCode(), v.getProduct().getName(), reason)));
            }
        } catch (Exception e) {
            // Nếu AI trả về sai format, fallback: gợi ý ngẫu nhiên 2 sản phẩm còn hàng đầu
            // tiên
            candidates.stream().limit(2).forEach(v -> result.add(
                    new com.example.lt_web2.dto.StyleSuggestionResponse(
                            v.getSkuCode(), v.getProduct().getName(), "Sản phẩm còn hàng phù hợp phối cùng")));
        }

        return result;
    }

    // ====== FR-AI-005: Tóm tắt hội thoại ======
    public com.example.lt_web2.dto.SummaryResponse summarizeConversation(Integer conversationId) {

        ChatConversation conversation = chatConversationRepository.findById(conversationId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy hội thoại"));

        List<ChatMessage> messages = chatMessageRepository.findByConversationIdOrderBySentAtAsc(conversationId);

        if (messages.isEmpty()) {
            return new com.example.lt_web2.dto.SummaryResponse("Chưa có tin nhắn nào trong hội thoại này.");
        }

        StringBuilder historyText = new StringBuilder();
        for (ChatMessage m : messages) {
            String sender = "CUSTOMER".equals(m.getSenderType()) ? "Khách"
                    : "AI".equals(m.getSenderType()) ? "AI" : "Nhân viên";
            historyText.append(sender).append(": ").append(m.getMessage()).append("\n");
        }

        String summaryPrompt = """
                Bạn là trợ lý tóm tắt hội thoại bán hàng cho shop thời trang.
                Đọc toàn bộ lịch sử chat dưới đây và tóm tắt CHÍNH XÁC đúng 3 dòng, mỗi dòng 1 ý, không thêm gì khác:
                Dòng 1: Khách muốn tìm đồ gì
                Dòng 2: Đã được tư vấn gì
                Dòng 3: Hành động tiếp theo cần làm là gì
                """;

        String summary = groqService.chatWithHistory(summaryPrompt, historyText.toString());
        return new com.example.lt_web2.dto.SummaryResponse(summary.trim());
    }

    // Helper: loại bỏ markdown code block AI hay tự thêm (```json ... ```)
    private String cleanJsonResponse(String raw) {
        return raw.replaceAll("```json", "").replaceAll("```", "").trim();
    }

    private String getTextOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull() || value.asText().equalsIgnoreCase("null"))
            return null;
        return value.asText();
    }
}