import { useToast } from '../use-toast';
import { useEffect } from 'react';

export default function Toaster() {
  const { toasts, dismiss } = useToast();

  useEffect(() => {
    // Auto-dismiss toasts after 5 seconds
    const timer = setTimeout(() => {
      toasts.forEach(toast => {
        if (toast.id) {
          dismiss(toast.id);
        }
      });
    }, 5000);

    return () => clearTimeout(timer);
  }, [toasts, dismiss]);

  if (!toasts.length) return null;

  return (
    <div className="fixed bottom-4 right-4 z-50 space-y-2">
      {toasts.map(({ id, title, description, variant = 'default' }) => (
        <div
          key={id}
          className={`p-4 rounded-lg shadow-lg min-w-64 ${
            variant === 'destructive'
              ? 'bg-red-500 text-white'
              : 'bg-white text-gray-800'
          }`}
        >
          <div className="flex justify-between items-start">
            <div>
              {title && <h3 className="font-semibold">{title}</h3>}
              {description && <p className="text-sm">{description}</p>}
            </div>
            <button
              onClick={() => id && dismiss(id)}
              className="text-current opacity-70 hover:opacity-100"
            >
              ✕
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}
