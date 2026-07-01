import { useEffect, useState } from 'react';
import {
  Table, Button, Modal, Form, Input, Select, DatePicker,
  message, Space, Tag, Popconfirm,
} from 'antd';
import { PlusOutlined, ReloadOutlined, LockOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import employeeApi from '../api/employeeApi';

export default function Employees() {
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [form] = Form.useForm();

  const fetchEmployees = async () => {
    setLoading(true);
    try {
      const res = await employeeApi.getAll();
      setEmployees(res.data);
    } catch (err) {
      message.error('Không tải được danh sách nhân viên');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEmployees();
  }, []);

  const handleOpenCreate = async () => {
    form.resetFields();
    try {
      const res = await employeeApi.generateCode();
      form.setFieldValue('employeeCode', res.data.employeeCode);
    } catch (err) {
      // không bắt buộc, nếu lỗi vẫn cho nhập tay
    }
    setModalOpen(true);
  };

  const handleCreate = async (values) => {
    const payload = {
      ...values,
      dateOfBirth: values.dateOfBirth ? values.dateOfBirth.format('YYYY-MM-DD') : null,
      startDate: values.startDate ? values.startDate.format('YYYY-MM-DD') : null,
    };
    try {
      await employeeApi.create(payload);
      message.success('Thêm nhân viên thành công!');
      setModalOpen(false);
      fetchEmployees();
    } catch (err) {
      const errMsg = err.response?.data?.message || 'Có lỗi xảy ra';
      message.error(errMsg);
    }
  };

  const handleDeactivate = async (id) => {
    try {
      await employeeApi.deactivate(id);
      message.success('Đã khóa tài khoản nhân viên!');
      fetchEmployees();
    } catch (err) {
      message.error('Có lỗi xảy ra khi khóa tài khoản');
    }
  };

  const columns = [
    { title: 'Mã NV', dataIndex: 'employeeCode', key: 'employeeCode' },
    { title: 'Họ tên', dataIndex: 'fullName', key: 'fullName' },
    { title: 'SĐT', dataIndex: 'phoneNumber', key: 'phoneNumber' },
    { title: 'Email', dataIndex: 'email', key: 'email' },
    {
      title: 'Vai trò',
      dataIndex: 'role',
      key: 'role',
      render: (role) => {
        const colorMap = { ADMIN: 'red', MANAGER: 'blue', STAFF: 'green' };
        return <Tag color={colorMap[role] || 'default'}>{role}</Tag>;
      },
    },
    { title: 'Chức vụ', dataIndex: 'position', key: 'position' },
    {
      title: 'Trạng thái',
      dataIndex: 'isActive',
      key: 'isActive',
      render: (active) =>
        active ? <Tag color="green">Đang làm việc</Tag> : <Tag color="red">Đã khóa</Tag>,
    },
    {
      title: 'Thao tác',
      key: 'action',
      render: (_, record) =>
        record.isActive ? (
          <Popconfirm
            title="Khóa tài khoản nhân viên này?"
            onConfirm={() => handleDeactivate(record.id)}
          >
            <Button danger icon={<LockOutlined />} size="small">
              Khóa
            </Button>
          </Popconfirm>
        ) : null,
    },
  ];

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleOpenCreate}>
          Thêm nhân viên
        </Button>
        <Button icon={<ReloadOutlined />} onClick={fetchEmployees}>
          Tải lại
        </Button>
      </Space>

      <Table rowKey="id" columns={columns} dataSource={employees} loading={loading} bordered />

      <Modal
        title="Thêm nhân viên mới"
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={() => form.submit()}
        okText="Lưu"
        cancelText="Hủy"
        width={600}
      >
        <Form form={form} layout="vertical" onFinish={handleCreate}>
          <Form.Item label="Mã nhân viên" name="employeeCode">
            <Input disabled />
          </Form.Item>

          <Form.Item
            label="Họ tên"
            name="fullName"
            rules={[{ required: true, message: 'Vui lòng nhập họ tên' }]}
          >
            <Input placeholder="Nguyễn Văn A" />
          </Form.Item>

          <Form.Item
            label="Ngày sinh"
            name="dateOfBirth"
            rules={[{ required: true, message: 'Vui lòng chọn ngày sinh' }]}
          >
            <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
          </Form.Item>

          <Form.Item label="Giới tính" name="gender">
            <Select
              placeholder="Chọn giới tính"
              options={[
                { label: 'Nam', value: 'Nam' },
                { label: 'Nữ', value: 'Nữ' },
                { label: 'Khác', value: 'Khác' },
              ]}
            />
          </Form.Item>

          <Form.Item
            label="Số điện thoại"
            name="phoneNumber"
            rules={[{ required: true, message: 'Vui lòng nhập số điện thoại' }]}
          >
            <Input placeholder="0901234567" />
          </Form.Item>

          <Form.Item
            label="Email"
            name="email"
            rules={[{ required: true, type: 'email', message: 'Email không hợp lệ' }]}
          >
            <Input placeholder="email@example.com" />
          </Form.Item>

          <Form.Item
            label="Mật khẩu"
            name="password"
            rules={[{ required: true, message: 'Vui lòng nhập mật khẩu' }]}
          >
            <Input.Password placeholder="Mật khẩu đăng nhập" />
          </Form.Item>

          <Form.Item label="Vai trò" name="role">
            <Select
              placeholder="STAFF (mặc định)"
              options={[
                { label: 'ADMIN', value: 'ADMIN' },
                { label: 'MANAGER', value: 'MANAGER' },
                { label: 'STAFF', value: 'STAFF' },
              ]}
            />
          </Form.Item>

          <Form.Item
            label="Mã PIN (6 số, dùng để chấm công)"
            name="pinCode"
            rules={[{ pattern: /^\d{6}$/, message: 'PIN phải gồm đúng 6 chữ số' }]}
          >
            <Input placeholder="123456" maxLength={6} />
          </Form.Item>

          <Form.Item label="Chức vụ" name="position">
            <Select
              placeholder="Chọn chức vụ"
              options={[
                { label: 'Admin', value: 'Admin' },
                { label: 'Quản lý cửa hàng', value: 'Quản lý cửa hàng' },
                { label: 'Nhân viên bán hàng', value: 'Nhân viên bán hàng' },
              ]}
            />
          </Form.Item>
          <Form.Item
            label="Ngày vào làm"
            name="startDate"
            rules={[{ required: true, message: 'Vui lòng chọn ngày vào làm' }]}
          >
            <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}