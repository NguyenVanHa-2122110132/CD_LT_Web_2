import { useEffect, useState } from 'react';
import {
  Table, Button, Modal, Form, Input, InputNumber, Select,
  message, Space, Tag, Image, Popconfirm, Divider,
} from 'antd';
import { PlusOutlined, ReloadOutlined, EyeOutlined } from '@ant-design/icons';
import productApi from '../api/productApi';
import categoryApi from '../api/categoryApi';
import brandApi from '../api/brandApi';

export default function Products() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [brands, setBrands] = useState([]);
  const [loading, setLoading] = useState(false);

  const [modalOpen, setModalOpen] = useState(false);
  const [variantModalOpen, setVariantModalOpen] = useState(false);
  const [variants, setVariants] = useState([]);
  const [selectedProductName, setSelectedProductName] = useState('');

  const [form] = Form.useForm();

  const fetchAll = async () => {
    setLoading(true);
    try {
      const [prodRes, catRes, brandRes] = await Promise.all([
        productApi.getAll(),
        categoryApi.getAll(),
        brandApi.getAll(),
      ]);
      console.log('IS ARRAY:', Array.isArray(prodRes.data), prodRes.data);
      setProducts(prodRes.data);
      setCategories(catRes.data);
      setBrands(brandRes.data);
    } catch (err) {
      message.error('Không tải được dữ liệu sản phẩm');
      console.error('FETCH ERROR:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAll();
  }, []);

  const handleViewVariants = async (product) => {
    try {
      const res = await productApi.getVariants(product.id);
      setVariants(res.data);
      setSelectedProductName(product.name);
      setVariantModalOpen(true);
    } catch (err) {
      message.error('Không tải được danh sách biến thể');
    }
  };

  const handleCreateProduct = async (values) => {
    const payload = {
      productCode: values.productCode,
      name: values.name,
      description: values.description,
      categoryId: values.categoryId,
      brandId: values.brandId,
      image: values.image,
      variants: values.variants || [],
    };
    try {
      await productApi.create(payload);
      message.success('Thêm sản phẩm thành công!');
      setModalOpen(false);
      form.resetFields();
      fetchAll();
    } catch (err) {
      const errMsg = err.response?.data?.message || 'Có lỗi xảy ra';
      message.error(errMsg);
    }
  };

  const columns = [
    { title: 'Mã SP', dataIndex: 'productCode', key: 'productCode' },
    { title: 'Tên sản phẩm', dataIndex: 'name', key: 'name' },
    {
      title: 'Danh mục',
      dataIndex: 'category',
      key: 'category',
      render: (cat) => cat?.name || '-',
    },
    {
      title: 'Thương hiệu',
      dataIndex: 'brand',
      key: 'brand',
      render: (b) => b?.name || '-',
    },
    {
      title: 'Ảnh',
      dataIndex: 'image',
      key: 'image',
      render: (img) => (img ? <Image src={img} width={50} /> : '-'),
    },
    {
      title: 'Thao tác',
      key: 'action',
      render: (_, record) => (
        <Button icon={<EyeOutlined />} onClick={() => handleViewVariants(record)}>
          Xem biến thể
        </Button>
      ),
    },
  ];

  const variantColumns = [
    { title: 'SKU', dataIndex: 'skuCode', key: 'skuCode' },
    { title: 'Màu', dataIndex: 'color', key: 'color' },
    { title: 'Size', dataIndex: 'size', key: 'size' },
    {
      title: 'Giá nhập', dataIndex: 'importPrice', key: 'importPrice',
      render: (v) => `${Number(v).toLocaleString()} đ`
    },
    {
      title: 'Giá bán', dataIndex: 'salePrice', key: 'salePrice',
      render: (v) => `${Number(v).toLocaleString()} đ`
    },
    {
      title: 'Tồn kho',
      dataIndex: 'stockQuantity',
      key: 'stockQuantity',
      render: (qty) => (
        <Tag color={qty > 10 ? 'green' : qty > 0 ? 'orange' : 'red'}>{qty}</Tag>
      ),
    },
  ];

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalOpen(true)}>
          Thêm sản phẩm
        </Button>
        <Button icon={<ReloadOutlined />} onClick={fetchAll}>
          Tải lại
        </Button>
      </Space>

      <Table rowKey="id" columns={columns} dataSource={products} loading={loading} bordered />

      {/* Modal: Thêm sản phẩm + biến thể */}
      <Modal
        title="Thêm sản phẩm mới"
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={() => form.submit()}
        okText="Lưu"
        cancelText="Hủy"
        width={800}
      >
        <Form form={form} layout="vertical" onFinish={handleCreateProduct}>
          <Space style={{ width: '100%' }} size="middle">
            <Form.Item
              label="Mã sản phẩm"
              name="productCode"
              rules={[{ required: true, message: 'Bắt buộc' }]}
              style={{ width: 200 }}
            >
              <Input placeholder="AO_POLO_01" />
            </Form.Item>
            <Form.Item
              label="Tên sản phẩm"
              name="name"
              rules={[{ required: true, message: 'Bắt buộc' }]}
              style={{ width: 300 }}
            >
              <Input placeholder="Áo Polo Nam" />
            </Form.Item>
          </Space>

          <Form.Item label="Mô tả" name="description">
            <Input.TextArea rows={2} />
          </Form.Item>

          <Space style={{ width: '100%' }} size="middle">
            <Form.Item label="Danh mục" name="categoryId" style={{ width: 250 }}>
              <Select
                placeholder="Chọn danh mục"
                options={categories.map((c) => ({ label: c.name, value: c.id }))}
              />
            </Form.Item>
            <Form.Item label="Thương hiệu" name="brandId" style={{ width: 250 }}>
              <Select
                placeholder="Chọn thương hiệu"
                options={brands.map((b) => ({ label: b.name, value: b.id }))}
              />
            </Form.Item>
          </Space>

          <Form.Item label="Link ảnh" name="image">
            <Input placeholder="https://..." />
          </Form.Item>

          <Divider>Danh sách biến thể (màu / size)</Divider>

          <Form.List name="variants">
            {(fields, { add, remove }) => (
              <>
                {fields.map(({ key, name, ...restField }) => (
                  <Space key={key} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
                    <Form.Item
                      {...restField}
                      name={[name, 'skuCode']}
                      rules={[{ required: true, message: 'SKU' }]}
                    >
                      <Input placeholder="SKU" style={{ width: 140 }} />
                    </Form.Item>
                    <Form.Item {...restField} name={[name, 'color']}>
                      <Input placeholder="Màu" style={{ width: 90 }} />
                    </Form.Item>
                    <Form.Item {...restField} name={[name, 'size']}>
                      <Input placeholder="Size" style={{ width: 70 }} />
                    </Form.Item>
                    <Form.Item {...restField} name={[name, 'importPrice']}>
                      <InputNumber placeholder="Giá nhập" style={{ width: 110 }} />
                    </Form.Item>
                    <Form.Item {...restField} name={[name, 'salePrice']}>
                      <InputNumber placeholder="Giá bán" style={{ width: 110 }} />
                    </Form.Item>
                    <Form.Item {...restField} name={[name, 'stockQuantity']}>
                      <InputNumber placeholder="Tồn kho" style={{ width: 90 }} />
                    </Form.Item>
                    <Popconfirm title="Xóa dòng này?" onConfirm={() => remove(name)}>
                      <Button danger size="small">Xóa</Button>
                    </Popconfirm>
                  </Space>
                ))}
                <Button type="dashed" onClick={() => add()} block>
                  + Thêm biến thể
                </Button>
              </>
            )}
          </Form.List>
        </Form>
      </Modal>

      {/* Modal: Xem danh sách biến thể */}
      <Modal
        title={`Biến thể của: ${selectedProductName}`}
        open={variantModalOpen}
        onCancel={() => setVariantModalOpen(false)}
        footer={null}
        width={700}
      >
        <Table
          rowKey="id"
          columns={variantColumns}
          dataSource={variants}
          pagination={false}
          bordered
        />
      </Modal>
    </div>
  );
}