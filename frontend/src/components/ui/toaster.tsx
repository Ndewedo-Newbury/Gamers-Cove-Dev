import * as React from "react"
import { useToast } from "./use-toast"

const Toaster = () => {
  const { toasts, dismiss } = useToast()

  return (
    <div className="fixed top-0 right-0 z-50 flex flex-col p-4 space-y-2">
      {toasts.map(({ id, title, description, variant }) => (
        <div
          key={id}
          className={
            "relative p-4 rounded-md shadow-lg w-80 " +
            (variant === 'destructive' 
              ? 'bg-red-50 border border-red-200 text-red-700' 
              : 'bg-white border border-gray-200 text-gray-900'
            )
          }
        >
          <div className="flex flex-col space-y-1">
            {title && <div className="font-medium">{title}</div>}
            {description && <div className="text-sm">{description}</div>}
          </div>
          <button 
            className="absolute top-2 right-2 text-gray-400 hover:text-gray-700"
            onClick={() => dismiss(id)}
            aria-label="Close"
          >
            ×
          </button>
        </div>
      ))}
    </div>
  )
}

export { Toaster as default }
