import { Link, useLocation } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { ShoppingCart, Menu } from 'lucide-react'
import { ThemeToggle } from '@/components/ui/theme-toggle'
import { cn } from '@/lib/utils'

export default function Navbar() {
  const location = useLocation()

  return (
    <nav className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container mx-auto flex h-16 items-center justify-between px-4">
        <Link to="/" className="flex items-center space-x-2">
          <span className="text-xl font-semibold tracking-tight">TCG Shop</span>
        </Link>
        
        <div className="hidden md:flex items-center gap-1">
          <Link to="/products">
            <Button 
              variant={location.pathname === '/products' ? 'secondary' : 'ghost'}
              className="text-sm font-medium"
            >
              Products
            </Button>
          </Link>
        </div>

        <div className="flex items-center gap-2">
          <Link to="/cart">
            <Button variant="ghost" size="icon" className="relative">
              <ShoppingCart className="h-5 w-5" />
            </Button>
          </Link>
          <ThemeToggle />
          <Link to="/login">
            <Button variant="outline" size="sm">Sign In</Button>
          </Link>
        </div>
      </div>
    </nav>
  )
}

