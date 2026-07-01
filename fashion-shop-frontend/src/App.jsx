import { BrowserRouter, Routes, Route } from 'react-router-dom';
import MainLayout from './components/MainLayout';
import Dashboard from './pages/Dashboard';
import Products from './pages/Products';
import Customers from './pages/Customers';
import Orders from './pages/Orders';
import AiAssistant from './pages/AiAssistant';
import Reports from './pages/Reports';
import Shifts from './pages/Shifts';
import Promotions from './pages/Promotions';
import Returns from './pages/Returns';
import Employees from './pages/Employees';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<MainLayout />}>
          <Route index element={<Dashboard />} />
          <Route path="products" element={<Products />} />
          <Route path="customers" element={<Customers />} />
          <Route path="orders" element={<Orders />} />
          <Route path="ai-assistant" element={<AiAssistant />} />
          <Route path="reports" element={<Reports />} />
          <Route path="shifts" element={<Shifts />} />
          <Route path="promotions" element={<Promotions />} />
          <Route path="returns" element={<Returns />} />
          <Route path="employees" element={<Employees />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;