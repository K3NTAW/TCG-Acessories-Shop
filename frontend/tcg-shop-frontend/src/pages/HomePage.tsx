import { Link } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { ArrowRight, Box, Shield, Sparkles } from 'lucide-react'

export default function HomePage() {
  return (
    <div className="flex flex-col">
      {/* Hero Section */}
      <section className="relative overflow-hidden border-b bg-gradient-to-b from-muted/50 to-background py-20 md:py-32">
        <div className="container mx-auto px-4">
          <div className="mx-auto max-w-3xl text-center space-y-6">
            <Badge variant="outline" className="mb-4">
              Premium 3D Printed Accessories
            </Badge>
            <h1 className="text-4xl font-bold tracking-tight sm:text-6xl md:text-7xl">
              Elevate Your
              <span className="block text-primary">TCG Collection</span>
            </h1>
            <p className="mx-auto max-w-2xl text-lg text-muted-foreground text-balance">
              Discover premium 3D-printed deck boxes, card holders, and storage solutions 
              designed for serious collectors and players.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center pt-4">
              <Link to="/products">
                <Button size="lg" className="group">
                  Shop Now
                  <ArrowRight className="ml-2 h-4 w-4 transition-transform group-hover:translate-x-1" />
                </Button>
              </Link>
              <Link to="/products">
                <Button size="lg" variant="outline">
                  Browse Collection
                </Button>
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="container mx-auto px-4 py-16 md:py-24">
        <div className="grid gap-8 md:grid-cols-3">
          <Card className="border-2 transition-all hover:shadow-lg">
            <CardHeader>
              <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
                <Box className="h-6 w-6 text-primary" />
              </div>
              <CardTitle>Deck Boxes</CardTitle>
              <CardDescription>
                Protect your decks with premium 3D-printed boxes designed for durability and style.
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Link to="/products?category=DECK_BOX">
                <Button variant="outline" className="w-full group">
                  Explore
                  <ArrowRight className="ml-2 h-4 w-4 transition-transform group-hover:translate-x-1" />
                </Button>
              </Link>
            </CardContent>
          </Card>

          <Card className="border-2 transition-all hover:shadow-lg">
            <CardHeader>
              <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
                <Shield className="h-6 w-6 text-primary" />
              </div>
              <CardTitle>Card Holders</CardTitle>
              <CardDescription>
                Display your favorite cards in elegant holders that showcase their beauty.
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Link to="/products?category=CARD_HOLDER">
                <Button variant="outline" className="w-full group">
                  Explore
                  <ArrowRight className="ml-2 h-4 w-4 transition-transform group-hover:translate-x-1" />
                </Button>
              </Link>
            </CardContent>
          </Card>

          <Card className="border-2 transition-all hover:shadow-lg">
            <CardHeader>
              <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
                <Sparkles className="h-6 w-6 text-primary" />
              </div>
              <CardTitle>Storage Solutions</CardTitle>
              <CardDescription>
                Organize your entire collection with smart storage systems built for scale.
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Link to="/products?category=STORAGE_SOLUTION">
                <Button variant="outline" className="w-full group">
                  Explore
                  <ArrowRight className="ml-2 h-4 w-4 transition-transform group-hover:translate-x-1" />
                </Button>
              </Link>
            </CardContent>
          </Card>
        </div>
      </section>
    </div>
  )
}
