import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast-container">
      @for (toast of toastService.toasts(); track toast.id) {
        <div class="toast" [class]="'toast-' + toast.type" (click)="toastService.dismiss(toast.id)">
          <span class="material-icons toast-icon">
            {{ toast.type === 'success' ? 'check_circle' : toast.type === 'error' ? 'error_outline' : 'info' }}
          </span>
          <span class="toast-message">{{ toast.message }}</span>
          <span class="material-icons toast-close">close</span>
        </div>
      }
    </div>
  `,
  styles: [`
    .toast-container {
      position: fixed;
      bottom: 1.5rem;
      right: 1.5rem;
      z-index: 9999;
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
      pointer-events: none;
    }

    .toast {
      display: flex;
      align-items: center;
      gap: 0.6rem;
      padding: 0.85rem 1.2rem;
      border-radius: 0.75rem;
      font-size: 0.88rem;
      font-weight: 500;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);
      pointer-events: auto;
      cursor: pointer;
      animation: slideIn 0.35s cubic-bezier(0.21, 1.02, 0.73, 1) forwards;
      max-width: 400px;
    }

    .toast-success {
      background: linear-gradient(135deg, #f0fdf4, #dcfce7);
      border: 1px solid #86efac;
      color: #166534;
    }

    .toast-error {
      background: linear-gradient(135deg, #fef2f2, #fee2e2);
      border: 1px solid #fca5a5;
      color: #dc2626;
    }

    .toast-info {
      background: linear-gradient(135deg, #f0f9ff, #e0f2fe);
      border: 1px solid #7dd3fc;
      color: #0369a1;
    }

    .toast-icon {
      font-size: 1.2rem;
      flex-shrink: 0;
    }

    .toast-message {
      flex: 1;
      line-height: 1.4;
    }

    .toast-close {
      font-size: 1rem;
      opacity: 0.5;
      flex-shrink: 0;
      transition: opacity 0.2s;
    }

    .toast:hover .toast-close {
      opacity: 1;
    }

    @keyframes slideIn {
      from {
        opacity: 0;
        transform: translateX(60px) scale(0.95);
      }
      to {
        opacity: 1;
        transform: translateX(0) scale(1);
      }
    }
  `]
})
export class ToastComponent {
  constructor(public toastService: ToastService) {}
}
