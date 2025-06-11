/**
 * Axios全局配置
 * 包含基础URL设置、请求/响应拦截器、错误处理等
 */

// 创建axios实例
const axiosInstance = axios.create({
    baseURL: '/',
    timeout: 10000, // 请求超时时间
    headers: {
        'Content-Type': 'application/json'
    }
});

// 请求拦截器
axiosInstance.interceptors.request.use(
    config => {
        // 在发送请求之前做些什么
        // 例如添加CSRF token
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
        
        if (csrfToken && csrfHeader) {
            config.headers[csrfHeader] = csrfToken;
        }
        
        // 添加加载指示器
        showLoading();
        
        return config;
    },
    error => {
        // 对请求错误做些什么
        hideLoading();
        console.error('请求发送失败:', error);
        showToast('请求发送失败，请检查网络连接', 'danger');
        return Promise.reject(error);
    }
);

// 响应拦截器
axiosInstance.interceptors.response.use(
    response => {
        // 对响应数据做点什么
        hideLoading();
        return response;
    },
    error => {
        // 对响应错误做点什么
        hideLoading();
        
        let errorMessage = '请求失败，请稍后重试';
        
        if (error.response) {
            // 服务器返回了错误状态码
            const status = error.response.status;
            
            switch (status) {
                case 401:
                    errorMessage = '未授权，请重新登录';
                    // 可以在这里处理重定向到登录页面
                    setTimeout(() => {
                        window.location.href = '/user/login';
                    }, 1500);
                    break;
                case 403:
                    errorMessage = '权限不足，无法访问该资源';
                    break;
                case 404:
                    errorMessage = '请求的资源不存在';
                    break;
                case 500:
                    errorMessage = '服务器内部错误';
                    break;
                default:
                    if (error.response.data && typeof error.response.data === 'string') {
                        errorMessage = error.response.data;
                    }
            }
        } else if (error.request) {
            // 请求已发出，但未收到响应
            errorMessage = '服务器无响应，请检查网络连接';
        }
        
        showToast(errorMessage, 'danger');
        console.error('请求错误:', error);
        
        return Promise.reject(error);
    }
);

// 加载指示器
let loadingCount = 0;
const loadingElement = document.createElement('div');
loadingElement.className = 'global-loading-indicator';
loadingElement.innerHTML = `
    <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">加载中...</span>
    </div>
`;
loadingElement.style.cssText = `
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(255, 255, 255, 0.7);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;
    opacity: 0;
    visibility: hidden;
    transition: opacity 0.3s, visibility 0.3s;
`;

document.body.appendChild(loadingElement);

function showLoading() {
    loadingCount++;
    if (loadingCount === 1) {
        loadingElement.style.opacity = '1';
        loadingElement.style.visibility = 'visible';
    }
}

function hideLoading() {
    loadingCount = Math.max(0, loadingCount - 1);
    if (loadingCount === 0) {
        loadingElement.style.opacity = '0';
        setTimeout(() => {
            if (loadingCount === 0) {
                loadingElement.style.visibility = 'hidden';
            }
        }, 300);
    }
}

// 全局Toast提示
function showToast(message, type = 'info') {
    const toastContainer = document.getElementById('toast-container') || createToastContainer();
    
    const toastId = 'toast-' + Date.now();
    const bgClass = type === 'success' ? 'bg-success' : 
                   type === 'danger' ? 'bg-danger' : 
                   type === 'warning' ? 'bg-warning' : 'bg-info';
    
    const toastHTML = `
        <div id="${toastId}" class="toast align-items-center ${bgClass} text-white border-0" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="关闭"></button>
            </div>
        </div>
    `;
    
    toastContainer.insertAdjacentHTML('beforeend', toastHTML);
    
    const toastElement = document.getElementById(toastId);
    if (typeof bootstrap !== 'undefined') {
        const toast = new bootstrap.Toast(toastElement, {
            autohide: true,
            delay: 3000
        });
        
        toast.show();
        
        // 自动删除toast元素
        toastElement.addEventListener('hidden.bs.toast', function() {
            toastElement.remove();
        });
    } else {
        // 如果bootstrap未加载，使用简单的替代方案
        toastElement.style.display = 'block';
        setTimeout(() => {
            toastElement.remove();
        }, 3000);
    }
}

function createToastContainer() {
    const container = document.createElement('div');
    container.id = 'toast-container';
    container.className = 'position-fixed bottom-0 end-0 p-3';
    container.style.zIndex = '5';
    document.body.appendChild(container);
    return container;
}

// 导出配置好的axios实例
window.http = axiosInstance; 