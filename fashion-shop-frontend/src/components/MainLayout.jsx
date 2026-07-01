import { useState } from 'react';
import { Layout, Menu, theme } from 'antd';
import {
    DashboardOutlined,
    UserOutlined,
    ShoppingCartOutlined,
    AppstoreOutlined,
    RobotOutlined,
    BarChartOutlined,
    ScheduleOutlined,
    TagsOutlined,
    SwapOutlined,
} from '@ant-design/icons';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { TeamOutlined } from '@ant-design/icons';

const { Header, Sider, Content } = Layout;

const menuItems = [
    { key: '/', icon: <DashboardOutlined />, label: 'Tổng quan' },
    { key: '/products', icon: <AppstoreOutlined />, label: 'Sản phẩm / Kho' },
    { key: '/customers', icon: <UserOutlined />, label: 'Khách hàng' },
    { key: '/employees', icon: <TeamOutlined />, label: 'Nhân viên' },
    { key: '/orders', icon: <ShoppingCartOutlined />, label: 'Đơn hàng (POS)' },
    { key: '/ai-assistant', icon: <RobotOutlined />, label: 'AI Assistant' },
    { key: '/reports', icon: <BarChartOutlined />, label: 'Thống kê / Báo cáo' },
    { key: '/shifts', icon: <ScheduleOutlined />, label: 'Ca & Chấm công' },
    { key: '/promotions', icon: <TagsOutlined />, label: 'Khuyến mãi / Voucher' },
    { key: '/returns', icon: <SwapOutlined />, label: 'Đổi trả & Hoàn tiền' },
];

export default function MainLayout() {
    const [collapsed, setCollapsed] = useState(false);
    const navigate = useNavigate();
    const location = useLocation();
    const {
        token: { colorBgContainer, borderRadiusLG },
    } = theme.useToken();

    return (
        <Layout style={{ minHeight: '100vh' }}>
            <Sider collapsible collapsed={collapsed} onCollapse={setCollapsed}>
                <div
                    style={{
                        height: 48,
                        margin: 16,
                        color: '#fff',
                        fontWeight: 'bold',
                        fontSize: collapsed ? 14 : 18,
                        textAlign: 'center',
                        whiteSpace: 'nowrap',
                        overflow: 'hidden',
                    }}
                >
                    {collapsed ? 'FS' : 'Fashion Shop'}
                </div>
                <Menu
                    theme="dark"
                    mode="inline"
                    selectedKeys={[location.pathname]}
                    items={menuItems}
                    onClick={({ key }) => navigate(key)}
                />
            </Sider>
            <Layout>
                <Header
                    style={{
                        padding: '0 24px',
                        background: colorBgContainer,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'space-between',
                    }}
                >
                    <span style={{ fontSize: 16, fontWeight: 500 }}>
                        Quản lý Shop Thời Trang
                    </span>
                    <span>Xin chào, Admin</span>
                </Header>
                <Content
                    style={{
                        margin: 24,
                        padding: 24,
                        minHeight: 280,
                        background: colorBgContainer,
                        borderRadius: borderRadiusLG,
                    }}
                >
                    <Outlet />
                </Content>
            </Layout>
        </Layout>
    );
}