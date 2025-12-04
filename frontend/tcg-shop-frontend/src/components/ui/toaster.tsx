import { createContext, useContext, useState, useCallback, ReactNode } from "react"
import { Toast } from "./toast"
import { X } from "lucide-react"

interface ToastProps {
  title?: string
  description?: string
  variant?: "default" | "destructive"
}

interface ToastContextType {
  toast: (props: ToastProps) => void
}

const ToastContext = createContext<ToastContextType | undefined>(undefined)

let toastState: Array<{ id: string } & ToastProps> = []
let toastListeners: Array<() => void> = []

const notifyListeners = () => {
  toastListeners.forEach(listener => listener())
}

export function ToasterProvider({ children }: { children: ReactNode }) {
  const [, setState] = useState(0)

  const addToast = useCallback((props: ToastProps) => {
    const id = Math.random().toString(36).substr(2, 9)
    toastState = [...toastState, { id, ...props }]
    notifyListeners()
    setState(s => s + 1)
    
    setTimeout(() => {
      toastState = toastState.filter((t) => t.id !== id)
      notifyListeners()
      setState(s => s + 1)
    }, 3000)
  }, [])

  const removeToast = useCallback((id: string) => {
    toastState = toastState.filter((t) => t.id !== id)
    notifyListeners()
    setState(s => s + 1)
  }, [])

  return (
    <ToastContext.Provider value={{ toast: addToast }}>
      {children}
      <div className="fixed bottom-0 z-[100] flex w-full flex-col-reverse p-4 sm:bottom-0 sm:right-0 sm:top-auto sm:flex-col md:max-w-[420px]">
        {toastState.map((toast) => (
          <Toast key={toast.id} variant={toast.variant} className="mb-2 group">
            <div className="grid gap-1 flex-1">
              {toast.title && <div className="text-sm font-semibold">{toast.title}</div>}
              {toast.description && <div className="text-sm opacity-90">{toast.description}</div>}
            </div>
            <button
              onClick={() => removeToast(toast.id)}
              className="absolute right-2 top-2 rounded-md p-1 text-foreground/50 opacity-0 transition-opacity hover:text-foreground focus:opacity-100 focus:outline-none focus:ring-2 group-hover:opacity-100"
            >
              <X className="h-4 w-4" />
            </button>
          </Toast>
        ))}
      </div>
    </ToastContext.Provider>
  )
}

export function useToast() {
  const context = useContext(ToastContext)
  if (!context) {
    // Fallback implementation
    return {
      toast: (props: ToastProps) => {
        console.log("Toast:", props)
        if (props.variant === "destructive") {
          alert(`Error: ${props.title || props.description}`)
        } else {
          alert(props.title || props.description || "Notification")
        }
      }
    }
  }
  return context
}

export function Toaster() {
  return null // Handled by ToasterProvider
}
