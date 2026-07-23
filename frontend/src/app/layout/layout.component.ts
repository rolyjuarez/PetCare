import { Component, signal, OnInit, OnDestroy } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../core/services/auth.service';
import { MenuService } from '../core/services/menu.service';
import { MenuItem } from '../core/models/menu.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './layout.component.html'
})
export class LayoutComponent implements OnInit, OnDestroy {
  sidebarOpen = signal(true);
  darkMode = signal(false);
  menuItems = signal<MenuItem[]>([]);
  expandedMenus = signal<Set<number>>(new Set());

  auth: AuthService;
  private sub?: Subscription;

  constructor(auth: AuthService, private menuService: MenuService) {
    this.auth = auth;
  }

  ngOnInit(): void {
    this.loadMenus();
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  loadMenus(): void {
    if (!this.auth.isAuthenticated()) return;
    this.sub = this.menuService.getMyMenus().subscribe({
      next: menus => this.menuItems.set(menus),
      error: () => this.menuItems.set([])
    });
  }

  toggleSidebar(): void {
    this.sidebarOpen.update(v => !v);
  }

  toggleDarkMode(): void {
    this.darkMode.update(v => !v);
    document.documentElement.classList.toggle('dark');
  }

  toggleMenu(menuId: number): void {
    this.expandedMenus.update(set => {
      const newSet = new Set(set);
      if (newSet.has(menuId)) {
        newSet.delete(menuId);
      } else {
        newSet.add(menuId);
      }
      return newSet;
    });
  }

  isMenuExpanded(menuId: number): boolean {
    return this.expandedMenus().has(menuId);
  }

  hasSubmenus(menu: MenuItem): boolean {
    return menu.submenus && menu.submenus.length > 0;
  }

  resolveUrl(menu: MenuItem): string {
    if (menu.url?.endsWith('dashboard')) {
      try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo') ?? 'null');
        if (userInfo?.roles?.includes('CLIENTE')) {
          return menu.url.replace(/dashboard$/, 'client-dashboard');
        }
      } catch {}
    }
    return menu.url;
  }
}
