import { useState } from 'react';
import {
  Input, Button, Table, InputNumber, Select, message, Card,
  Row, Col, Statistic, Divider, Tag, Modal,
} from 'antd';
import { SearchOutlined, DeleteOutlined, ShoppingCartOutlined } from '@ant-design/icons';
import productApi from '../api/productApi';
import customerApi from '../api/customerApi';
import orderApi from '../api/orderApi';

export default function Orders() {
  const [skuInput, setSkuInput] = useState('');
  const [cartItems, setCartItems] = useState([]); // { variantId, skuCode, name, color, size, salePrice, quantity, stockQuantity }
  const [searching, setSearching] = useState(false);

  const [customers, setCustomers] = useState([]);
  const [selectedCustomerId, setSelectedCustomerId] = useState(null);
  const [customerLoaded, setCustomerLoaded] = useState(false);

  const [paymentMethod, setPaymentMethod] = useState('CASH');
  const [submitting, setSubmitting] = useState(false);
  const [lastOrder, setLastOrder] = useState(null);

  const loadCustomers = async () => {
    if (customerLoaded) return;
    try {
      const res = await customerApi.getAll();
      setCustomers(res.data);
      setCustomerLoaded(true);
    } catch (err) {
      message.error('Không tải được danh sách khách hàng');
    }
  };

  const handleSearchSku = async () => {
    if (!skuInput.trim()) return;
    setSearching(true);
    try {
      const res = await productApi.findBySku(skuInput.trim());
      if (!res.data.found) {
        message.warning('Không tìm thấy sản phẩm với mã vạch này!');
        return;
      }
      const v = res.data.variant;

      setCartItems((prev) => {
        const existing = prev.find((item) => item.variantId === v.id);
        if (existing) {
          if (existing.quantity + 1 > v.stockQuantity) {
            message.warning(`Sản phẩm "${v.skuCode}" chỉ còn ${v.stockQuantity} trong kho.`);
            return prev;
          }
          return prev.map((item) =>
            item.variantId === v.id ? { ...item, quantity: item.quantity + 1 } : item
          );
        }
        return [
          ...prev,
          {
            variantId: v.id,
            skuCode: v.skuCode,
            name: v.product?.name || v.skuCode,
            color: v.color,
            size: v.size,
            salePrice: v.salePrice,
            quantity: 1,
            stockQuantity: v.stockQuantity,
          },
        ];
      });
      setSkuInput('');
    } catch (err) {
      message.error('Không tìm thấy sản phẩm với mã vạch này!');
    } finally {
      setSearching(false);
    }
  };

  const updateQuantity = (variantId, qty) => {
    setCartItems((prev) =>
      prev.map((item) => {
        if (item.variantId !== variantId) return item;
        if (qty > item.stockQuantity) {
          message.warning(`Chỉ còn ${item.stockQuantity} sản phẩm trong kho.`);
          return { ...item, quantity: item.stockQuantity };
        }
        return { ...item, quantity: qty };
      })
    );
  };

  const removeItem = (variantId) => {
    setCartItems((prev) => prev.filter((item) => item.variantId !== variantId));
  };

  const totalAmount = cartItems.reduce((sum, item) => sum + item.salePrice * item.quantity, 0);

  const handleSubmitOrder = async () => {
    if (cartItems.length === 0) {
      message.warning('Giỏ hàng đang trống.');
      return;
    }
    setSubmitting(true);
    try {
      const payload = {
        customerId: selectedCustomerId || null,
        employeeId: 1, // TODO: lấy từ tài khoản đăng nhập thực tế khi có Auth
        items: cartItems.map((item) => ({
          variantId: item.variantId,
          quantity: item.quantity,
        })),
        paymentMethod,
      };
      const res = await orderApi.create(payload);
      const order = res.data;

      // Hoàn tất thanh toán ngay (CASH)
      const completedRes = await orderApi.complete(order.id, paymentMethod);
      setLastOrder(completedRes.data);

      message.success(`Tạo đơn hàng ${completedRes.data.orderCode} thành công!`);
      setCartItems([]);
      setSelectedCustomerId(null);
    } catch (err) {
      const errMsg = err.response?.data?.message || 'Có lỗi xảy ra khi tạo đơn hàng';
      message.error(errMsg);
    } finally {
      setSubmitting(false);
    }
  };

  const cartColumns = [
    { title: 'SKU', dataIndex: 'skuCode', key: 'skuCode' },
    { title: 'Tên SP', dataIndex: 'name', key: 'name' },
    { title: 'Màu', dataIndex: 'color', key: 'color' },
    { title: 'Size', dataIndex: 'size', key: 'size' },
    {
      title: 'Đơn giá',
      dataIndex: 'salePrice',
      key: 'salePrice',
      render: (v) => `${Number(v).toLocaleString()} đ`,
    },
    {
      title: 'Số lượng',
      key: 'quantity',
      render: (_, record) => (
        <InputNumber
          min={1}
          max={record.stockQuantity}
          value={record.quantity}
          onChange={(val) => updateQuantity(record.variantId, val)}
        />
      ),
    },
    {
      title: 'Thành tiền',
      key: 'subtotal',
      render: (_, record) => `${(record.salePrice * record.quantity).toLocaleString()} đ`,
    },
    {
      title: '',
      key: 'action',
      render: (_, record) => (
        <Button danger icon={<DeleteOutlined />} onClick={() => removeItem(record.variantId)} />
      ),
    },
  ];

  return (
    <div>
      <Row gutter={24}>
        <Col span={16}>
          <Card title="Quét mã vạch / Nhập SKU sản phẩm">
            <Input.Search
              placeholder="Nhập mã SKU (VD: AO_POLO_NAM_01_DEN_M)"
              value={skuInput}
              onChange={(e) => setSkuInput(e.target.value)}
              onSearch={handleSearchSku}
              enterButton={<SearchOutlined />}
              loading={searching}
              size="large"
            />
          </Card>

          <Card title="Giỏ hàng" style={{ marginTop: 16 }}>
            <Table
              rowKey="variantId"
              columns={cartColumns}
              dataSource={cartItems}
              pagination={false}
              locale={{ emptyText: 'Chưa có sản phẩm nào trong giỏ' }}
            />
          </Card>
        </Col>

        <Col span={8}>
          <Card title="Thông tin đơn hàng">
            <div style={{ marginBottom: 16 }}>
              <label>Khách hàng (bỏ trống = khách vãng lai)</label>
              <Select
                style={{ width: '100%', marginTop: 8 }}
                placeholder="Chọn khách hàng"
                allowClear
                showSearch
                optionFilterProp="label"
                onFocus={loadCustomers}
                value={selectedCustomerId}
                onChange={setSelectedCustomerId}
                options={customers.map((c) => ({
                  label: `${c.fullName} - ${c.phoneNumber}`,
                  value: c.id,
                }))}
              />
            </div>

            <div style={{ marginBottom: 16 }}>
              <label>Hình thức thanh toán</label>
              <Select
                style={{ width: '100%', marginTop: 8 }}
                value={paymentMethod}
                onChange={setPaymentMethod}
                options={[
                  { label: 'Tiền mặt', value: 'CASH' },
                  { label: 'Chuyển khoản QR', value: 'QR' },
                ]}
              />
            </div>

            <Divider />

            <Statistic
              title="Tổng tiền thực thu"
              value={totalAmount}
              suffix="đ"
              valueStyle={{ color: '#1677ff' }}
            />

            <Button
              type="primary"
              icon={<ShoppingCartOutlined />}
              size="large"
              block
              style={{ marginTop: 16 }}
              loading={submitting}
              onClick={handleSubmitOrder}
            >
              Xác nhận thanh toán
            </Button>
          </Card>
        </Col>
      </Row>

      <Modal
        title="Đơn hàng đã hoàn tất"
        open={!!lastOrder}
        onCancel={() => setLastOrder(null)}
        footer={[
          <Button key="close" onClick={() => setLastOrder(null)}>
            Đóng
          </Button>,
        ]}
      >
        {lastOrder && (
          <div>
            <p><strong>Mã hóa đơn:</strong> {lastOrder.orderCode}</p>
            <p><strong>Khách hàng:</strong> {lastOrder.customerName}</p>
            <p><strong>Tổng tiền:</strong> {Number(lastOrder.finalAmount).toLocaleString()} đ</p>
            <p><strong>Trạng thái:</strong> <Tag color="green">{lastOrder.status}</Tag></p>
          </div>
        )}
      </Modal>
    </div>
  );
}