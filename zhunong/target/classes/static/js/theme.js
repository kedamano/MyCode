/**
 * 主题切换功能
 * 支持自动检测系统主题偏好并提供手动切换选项
 */
document.addEventListener('DOMContentLoaded', function() {
    const themeToggle = document.getElementById('theme-toggle');
    const body = document.body;
    const themeEvent = new Event('themeChanged');
    
    // 检查本地存储中的主题设置
    const isDarkMode = localStorage.getItem('darkMode') === 'true';
    const prefersDarkScheme = window.matchMedia('(prefers-color-scheme: dark)');
    
    // 应用主题设置
    function applyTheme(isDark) {
        if (isDark) {
            body.classList.add('dark-mode');
            updateThemeToggleIcon('dark', themeToggle);
            document.querySelector('meta[name="theme-color"]')?.setAttribute('content', '#121212');
        } else {
            body.classList.remove('dark-mode');
            updateThemeToggleIcon('light', themeToggle);
            document.querySelector('meta[name="theme-color"]')?.setAttribute('content', '#ffffff');
        }
        
        // 触发主题变更事件，让图表等组件响应主题变化
        document.dispatchEvent(themeEvent);
    }
    
    // 在页面头部添加主题颜色元标签
    if (!document.querySelector('meta[name="theme-color"]')) {
        const metaThemeColor = document.createElement('meta');
        metaThemeColor.name = 'theme-color';
        metaThemeColor.content = isDarkMode ? '#121212' : '#ffffff';
        document.head.appendChild(metaThemeColor);
    }
    
    // 应用保存的主题设置或系统偏好
    if (localStorage.getItem('darkMode') !== null) {
        applyTheme(isDarkMode);
    } else {
        applyTheme(prefersDarkScheme.matches);
    }
    
    // 监听主题切换按钮点击事件
    if (themeToggle) {
        themeToggle.addEventListener('click', function() {
            const isDark = body.classList.contains('dark-mode');
            localStorage.setItem('darkMode', (!isDark).toString());
            applyTheme(!isDark);
            
            // 添加过渡动画
            body.style.transition = 'background-color 0.3s ease, color 0.3s ease';
            setTimeout(() => {
                body.style.transition = '';
            }, 300);
        });
    }
    
    // 监听系统主题变化
    prefersDarkScheme.addEventListener('change', function(e) {
        // 只有当用户没有手动设置主题时才自动切换
        if (localStorage.getItem('darkMode') === null) {
            applyTheme(e.matches);
        }
    });
    
    // 添加键盘快捷键支持
    document.addEventListener('keydown', function(e) {
        // Alt+Shift+D 切换暗色模式
        if (e.altKey && e.shiftKey && e.code === 'KeyD') {
            e.preventDefault();
            themeToggle.click();
        }
    });
});

// 更新主题切换按钮图标
function updateThemeToggleIcon(theme, button) {
    if (!button) return;
    
    if (theme === 'dark') {
        button.innerHTML = '<i class="bi bi-sun"></i>';
        button.setAttribute('title', '切换到亮色模式 (Alt+Shift+D)');
        button.setAttribute('aria-label', '当前为暗色模式，点击切换到亮色模式');
    } else {
        button.innerHTML = '<i class="bi bi-moon"></i>';
        button.setAttribute('title', '切换到暗色模式 (Alt+Shift+D)');
        button.setAttribute('aria-label', '当前为亮色模式，点击切换到暗色模式');
    }
} 