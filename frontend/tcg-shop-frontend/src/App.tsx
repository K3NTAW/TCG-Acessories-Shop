import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import { ToasterProvider } from '@/components/ui/toaster'
import Navbar from '@/components/layout/Navbar'
import HomePage from '@/pages/HomePage'
import ProductsPage from '@/pages/ProductsPage'
import ProductDetailPage from '@/pages/ProductDetailPage'
import CartPage from '@/pages/CartPage'
import CheckoutPage from '@/pages/CheckoutPage'
import LoginPage from '@/pages/LoginPage'
import RegisterPage from '@/pages/RegisterPage'
import AdminPage from '@/pages/AdminPage'

function App() {
  return (
    <ToasterProvider>
      <Router>
        <div className="min-h-screen bg-background">
          <Navbar />
          <main>
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/products" element={<ProductsPage />} />
              <Route path="/products/:id" element={<ProductDetailPage />} />
              <Route path="/cart" element={<CartPage />} />
              <Route path="/checkout" element={<CheckoutPage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />
              <Route path="/admin" element={<AdminPage />} />
            </Routes>
          </main>
        </div>
      </Router>
    </ToasterProvider>
  )
}

export default App

