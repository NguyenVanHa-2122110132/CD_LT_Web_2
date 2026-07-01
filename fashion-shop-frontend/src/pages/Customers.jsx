import { useEffect, useState } from 'react';
import { Table, Button, Modal, Form, Input, message, Space, Tag } from 'antd';
import { PlusOutlined, ReloadOutlined } from '@ant-design/icons';
import axiosClient from '../api/axiosClient';
import customerApi from '../api/customerApi';

export default function Customers() {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [form] = Form.useForm();

  const fetchCustomers = async () => {
    setLoading(true);
    try {
      const res = await axiosClient.get('/customers');
      setCustomers(res.data);
    } catch (err) {
      message.error('Không tải được danh sách khách hàng');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCustomers();
  }, []);

  const handleAddCustomer = async (values) => {
    try {
      await customerApi.quickAdd(values);
      message.success('Thêm khách hàng thành công!');
      setModalOpen(false);
      form.resetFields();
      fetchCustomers();
    } catch (err) {
      const errMsg = err.response?.data?.message || 'Có lỗi xảy ra';
      message.error(errMsg);
    }
  };

  const columns = [
    { title: 'Mã KH', dataIndex: 'customerCode', key: 'customerCode' },
    { title: 'Họ tên', dataIndex: 'fullName', key: 'fullName' },
    { title: 'Số điện thoại', dataIndex: 'phoneNumber', key: 'phoneNumber' },
    { title: 'Email', dataIndex: 'email', key: 'email' },
    { title: 'Địa chỉ', dataIndex: 'address', key: 'address' },
    {
      title: 'Điểm tích lũy',
      dataIndex: 'totalPoints',
      key: 'totalPoints',
      render: (points) => <Tag color="gold">{points} điểm</Tag>,
    },
  ];

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalOpen(true)}>
          Thêm khách hàng
        </Button>
        <Button icon={<ReloadOutlined />} onClick={fetchCustomers}>
          Tải lại
        </Button>
      </Space>

      <Table
        rowKey="id"
        columns={columns}
        dataSource={customers}
        loading={loading}
        bordered
      />

      <Modal
        title="Thêm khách hàng mới"
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={() => form.submit()}
        okText="Lưu"
        cancelText="Hủy"
      >
        <Form form={form} layout="vertical" onFinish={handleAddCustomer}>
          <Form.Item
            label="Họ tên"
            name="fullName"
            rules={[{ required: true, message: 'Vui lòng nhập họ tên' }]}
          >
            <Input placeholder="Nguyễn Văn A" />
          </Form.Item>
          <Form.Item
            label="Số điện thoại"
            name="phoneNumber"
            rules={[{ required: true, message: 'Vui lòng nhập số điện thoại' }]}
          >
            <Input placeholder="0901234567" />
          </Form.Item>
          <Form.Item label="Email" name="email">
            <Input placeholder="email@example.com" />
          </Form.Item>
          <Form.Item label="Địa chỉ" name="address">
            <Input placeholder="123 Lê Lợi, Q1" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}