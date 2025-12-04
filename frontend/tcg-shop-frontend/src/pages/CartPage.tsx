import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { cartApi } from '@/lib/api'
import { getSessionId } from '@/lib/utils'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Separator } from '@/components/ui/separator'
import { Skeleton } from '@/components/ui/skeleton'
import { Badge } from '@/components/ui/badge'
import { ShoppingCart, Trash2, ArrowRight, ShoppingBag } from 'lucide-react'
import { useToast } from '@/components/ui/use-toast'

export default function CartPage() {
  const [cart, setCart] = useState<any>(null)
  const [loading, setLoading] = useState(true)
  const [removing, setRemoving] = useState<number | null>(null)
  const navigate = useNavigate()
  const { toast } = useToast()

  useEffect(() => {
    const sessionId = getSessionId()
    cartApi.get(sessionId)
      .then(res => {
        setCart(res.data)
        setLoading(false)
      })
      .catch(err => {
        console.error('Error fetching cart:', err)
        setLoading(false)
      })
  }, [])

  const handleRemove = (productId: number) => {
    setRemoving(productId)
    const sessionId = getSessionId()
    cartApi.removeItem(sessionId, productId)
      .then(() => {
        setCart((prev: any) => ({
          ...prev,
          items: prev.items.filter((item: any) => item.productId !== productId),
          total: prev.total - (prev.items.find((item: any) => item.productId === productId)?.price || 0) * (prev.items.find((item: any) => item.productId === productId)?.quantity || 0)
        }))
        toast({
          title: "Item removed",
          description: "Item has been removed from your cart.",
        })
      })
      .catch(err => {
        console.error('Error removing item:', err)
        toast({
          title: "Error",
          description: "Failed to remove item. Please try again.",
          variant: "destructive",
        })
      })
      .finally(() => {
        setRemoving(null)
      })
  }

  if (loading) {
    return (
      <div className="container mx-auto px-4 py-12">
        <Skeleton className="h-10 w-48 mb-8" />
        <Card>
          <CardHeader>
            <Skeleton className="h-6 w-32" />
          </CardHeader>
          <CardContent className="space-y-4">
            {[...Array(3)].map((_, i) => (
              <div key={i} className="flex gap-4">
                <Skeleton className="h-24 w-24 rounded-lg" />
                <div className="flex-1 space-y-2">
                  <Skeleton className="h-6 w-3/4" />
                  <Skeleton className="h-4 w-1/2" />
                </div>
                <Skeleton className="h-10 w-24" />
              </div>
            ))}
          </CardContent>
        </Card>
      </div>
    )
  }

  if (!cart || !cart.items || cart.items.length === 0) {
    return (
      <div className="container mx-auto px-4 py-12">
        <div className="flex flex-col items-center justify-center py-24 text-center">
          <ShoppingBag className="h-24 w-24 text-muted-foreground/30 mb-6" />
          <h2 className="text-2xl font-semibold mb-2">Your cart is empty</h2>
          <p className="text-muted-foreground mb-6">
            Start shopping to add items to your cart
          </p>
          <Button onClick={() => navigate('/products')}>
            Browse Products
            <ArrowRight className="ml-2 h-4 w-4" />
          </Button>
        </div>
      </div>
    )
  }

  return (
    <div className="container mx-auto px-4 py-12">
      <div className="mb-8">
        <h1 className="text-4xl font-bold tracking-tight mb-2">Shopping Cart</h1>
        <p className="text-muted-foreground">
          {cart.items.length} {cart.items.length === 1 ? 'item' : 'items'} in your cart
        </p>
      </div>

      <div className="grid gap-8 lg:grid-cols-3">
        <div className="lg:col-span-2 space-y-4">
          {cart.items.map((item: any) => (
            <Card key={item.productId} className="overflow-hidden">
              <CardContent className="p-6">
                <div className="flex gap-6">
                  <div className="h-24 w-24 rounded-lg bg-muted flex items-center justify-center flex-shrink-0">
                    <ShoppingCart className="h-12 w-12 text-muted-foreground/30" />
                  </div>
                  
                  <div className="flex-1 min-w-0">
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <h3 className="font-semibold text-lg mb-1">{item.productName}</h3>
                        <p className="text-sm text-muted-foreground mb-2">
                          ${item.price} each
                        </p>
                        <Badge variant="secondary">
                          Quantity: {item.quantity}
                        </Badge>
                      </div>
                      
                      <div className="flex flex-col items-end gap-2">
                        <p className="text-lg font-semibold">
                          ${(item.price * item.quantity).toFixed(2)}
                        </p>
                        <Button
                          variant="ghost"
                          size="icon"
                          onClick={() => handleRemove(item.productId)}
                          disabled={removing === item.productId}
                          className="text-destructive hover:text-destructive"
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </div>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        <div className="lg:col-span-1">
          <Card className="sticky top-24">
            <CardHeader>
              <CardTitle>Order Summary</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">Subtotal</span>
                  <span>${cart.total.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">Shipping</span>
                  <span>Calculated at checkout</span>
                </div>
              </div>
              
              <Separator />
              
              <div className="flex justify-between text-lg font-semibold">
                <span>Total</span>
                <span>${cart.total.toFixed(2)}</span>
              </div>
              
              <Button 
                size="lg" 
                className="w-full mt-4"
                onClick={() => navigate('/checkout')}
              >
                Proceed to Checkout
                <ArrowRight className="ml-2 h-4 w-4" />
              </Button>
              
              <Button 
                variant="outline" 
                className="w-full"
                onClick={() => navigate('/products')}
              >
                Continue Shopping
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}
