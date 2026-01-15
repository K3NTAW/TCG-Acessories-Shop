import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { cartApi, orderApi } from '@/lib/api'
import { getSessionId } from '@/lib/utils'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Separator } from '@/components/ui/separator'
import { Skeleton } from '@/components/ui/skeleton'
import { Badge } from '@/components/ui/badge'
import { ArrowLeft, ShoppingCart, CheckCircle2, Loader2 } from 'lucide-react'
import { useToast } from '@/components/ui/use-toast'

export default function CheckoutPage() {
  const [cart, setCart] = useState<any>(null)
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [orderCreated, setOrderCreated] = useState(false)
  const [orderNumber, setOrderNumber] = useState<string | null>(null)
  const navigate = useNavigate()
  const { toast } = useToast()

  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    address: '',
    city: '',
    state: '',
    zipCode: '',
    country: '',
  })

  useEffect(() => {
    const sessionId = getSessionId()
    cartApi.get(sessionId)
      .then(res => {
        const cartData = res.data
        if (!cartData || !cartData.items || cartData.items.length === 0) {
          toast({
            title: "Cart is empty",
            description: "Please add items to your cart before checkout.",
            variant: "destructive",
          })
          navigate('/cart')
          return
        }
        setCart(cartData)
        setLoading(false)
      })
      .catch(err => {
        console.error('Error fetching cart:', err)
        toast({
          title: "Error",
          description: "Failed to load cart. Please try again.",
          variant: "destructive",
        })
        navigate('/cart')
      })
  }, [navigate, toast])

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    })
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!cart || !cart.items || cart.items.length === 0) {
      toast({
        title: "Cart is empty",
        description: "Please add items to your cart before checkout.",
        variant: "destructive",
      })
      return
    }

    setSubmitting(true)

    try {
      // Convert cart items to order items
      const orderItems = cart.items.map((item: any) => ({
        productId: item.productId,
        quantity: item.quantity,
        unitPrice: item.price,
      }))

      // Create order (using customerId = 1 for now, since we don't have full auth)
      const orderData = {
        customerId: 1,
        shippingAddressId: null, // Optional
        items: orderItems,
      }

      const response = await orderApi.create(orderData)
      
      setOrderNumber(response.data.orderNumber)
      setOrderCreated(true)

      // Clear the cart after successful order
      const sessionId = getSessionId()
      await cartApi.clear(sessionId)

      toast({
        title: "Order placed successfully!",
        description: `Your order ${response.data.orderNumber} has been placed.`,
      })
    } catch (err: any) {
      console.error('Error creating order:', err)
      toast({
        title: "Error",
        description: err.response?.data?.message || "Failed to place order. Please try again.",
        variant: "destructive",
      })
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return (
      <div className="container mx-auto px-4 py-12">
        <Skeleton className="h-10 w-48 mb-8" />
        <div className="grid gap-8 lg:grid-cols-2">
          <Card>
            <CardHeader>
              <Skeleton className="h-6 w-32" />
            </CardHeader>
            <CardContent className="space-y-4">
              {[...Array(5)].map((_, i) => (
                <Skeleton key={i} className="h-12 w-full" />
              ))}
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <Skeleton className="h-6 w-32" />
            </CardHeader>
            <CardContent>
              <Skeleton className="h-24 w-full" />
            </CardContent>
          </Card>
        </div>
      </div>
    )
  }

  if (orderCreated) {
    return (
      <div className="container mx-auto px-4 py-12">
        <div className="max-w-2xl mx-auto">
          <Card>
            <CardContent className="pt-6">
              <div className="flex flex-col items-center justify-center py-12 text-center">
                <div className="rounded-full bg-green-100 dark:bg-green-900 p-4 mb-6">
                  <CheckCircle2 className="h-16 w-16 text-green-600 dark:text-green-400" />
                </div>
                <h1 className="text-3xl font-bold mb-2">Order Placed Successfully!</h1>
                <p className="text-muted-foreground mb-6">
                  Thank you for your purchase. Your order has been received.
                </p>
                {orderNumber && (
                  <Badge variant="secondary" className="text-lg px-4 py-2 mb-6">
                    Order Number: {orderNumber}
                  </Badge>
                )}
                <div className="flex gap-4">
                  <Button onClick={() => navigate('/products')}>
                    Continue Shopping
                  </Button>
                  <Button variant="outline" onClick={() => navigate('/')}>
                    Back to Home
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    )
  }

  if (!cart || !cart.items || cart.items.length === 0) {
    return (
      <div className="container mx-auto px-4 py-12">
        <div className="flex flex-col items-center justify-center py-24 text-center">
          <ShoppingCart className="h-24 w-24 text-muted-foreground/30 mb-6" />
          <h2 className="text-2xl font-semibold mb-2">Your cart is empty</h2>
          <p className="text-muted-foreground mb-6">
            Please add items to your cart before checkout
          </p>
          <Button onClick={() => navigate('/products')}>
            Browse Products
          </Button>
        </div>
      </div>
    )
  }

  return (
    <div className="container mx-auto px-4 py-12">
      <div className="mb-8">
        <Button
          variant="ghost"
          onClick={() => navigate('/cart')}
          className="mb-4"
        >
          <ArrowLeft className="mr-2 h-4 w-4" />
          Back to Cart
        </Button>
        <h1 className="text-4xl font-bold tracking-tight mb-2">Checkout</h1>
        <p className="text-muted-foreground">
          Please fill in your shipping information
        </p>
      </div>

      <form onSubmit={handleSubmit}>
        <div className="grid gap-8 lg:grid-cols-2">
          {/* Shipping Information */}
          <div className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Shipping Information</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="grid gap-4 sm:grid-cols-2">
                  <div className="space-y-2">
                    <Label htmlFor="firstName">First Name *</Label>
                    <Input
                      id="firstName"
                      name="firstName"
                      required
                      value={formData.firstName}
                      onChange={handleInputChange}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="lastName">Last Name *</Label>
                    <Input
                      id="lastName"
                      name="lastName"
                      required
                      value={formData.lastName}
                      onChange={handleInputChange}
                    />
                  </div>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="email">Email *</Label>
                  <Input
                    id="email"
                    name="email"
                    type="email"
                    required
                    value={formData.email}
                    onChange={handleInputChange}
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="phone">Phone *</Label>
                  <Input
                    id="phone"
                    name="phone"
                    type="tel"
                    required
                    value={formData.phone}
                    onChange={handleInputChange}
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="address">Address *</Label>
                  <Input
                    id="address"
                    name="address"
                    required
                    value={formData.address}
                    onChange={handleInputChange}
                  />
                </div>

                <div className="grid gap-4 sm:grid-cols-2">
                  <div className="space-y-2">
                    <Label htmlFor="city">City *</Label>
                    <Input
                      id="city"
                      name="city"
                      required
                      value={formData.city}
                      onChange={handleInputChange}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="state">State/Province *</Label>
                    <Input
                      id="state"
                      name="state"
                      required
                      value={formData.state}
                      onChange={handleInputChange}
                    />
                  </div>
                </div>

                <div className="grid gap-4 sm:grid-cols-2">
                  <div className="space-y-2">
                    <Label htmlFor="zipCode">ZIP/Postal Code *</Label>
                    <Input
                      id="zipCode"
                      name="zipCode"
                      required
                      value={formData.zipCode}
                      onChange={handleInputChange}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="country">Country *</Label>
                    <Input
                      id="country"
                      name="country"
                      required
                      value={formData.country}
                      onChange={handleInputChange}
                    />
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Order Summary */}
          <div className="space-y-6">
            <Card className="sticky top-24">
              <CardHeader>
                <CardTitle>Order Summary</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-3">
                  {cart.items.map((item: any) => (
                    <div key={item.productId} className="flex justify-between items-start">
                      <div className="flex-1">
                        <p className="font-medium">{item.productName}</p>
                        <p className="text-sm text-muted-foreground">
                          Quantity: {item.quantity} × ${item.price}
                        </p>
                      </div>
                      <p className="font-semibold">
                        ${(item.price * item.quantity).toFixed(2)}
                      </p>
                    </div>
                  ))}
                </div>

                <Separator />

                <div className="space-y-2">
                  <div className="flex justify-between text-sm">
                    <span className="text-muted-foreground">Subtotal</span>
                    <span>${cart.total.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-muted-foreground">Shipping</span>
                    <span>Free</span>
                  </div>
                </div>

                <Separator />

                <div className="flex justify-between text-lg font-semibold">
                  <span>Total</span>
                  <span>${cart.total.toFixed(2)}</span>
                </div>

                <Button
                  type="submit"
                  size="lg"
                  className="w-full mt-4"
                  disabled={submitting}
                >
                  {submitting ? (
                    <>
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                      Placing Order...
                    </>
                  ) : (
                    'Place Order'
                  )}
                </Button>
              </CardContent>
            </Card>
          </div>
        </div>
      </form>
    </div>
  )
}

