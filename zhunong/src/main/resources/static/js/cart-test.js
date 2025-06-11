/**
 * 购物车测试脚本
 * 用于测试购物车功能是否正常工作
 */
(function() {
    console.log('购物车测试脚本已加载');
    
    // 测试购物车状态
    function testCartStatus() {
        console.log('正在检查购物车状态...');
        fetch('/cart/debug/status')
            .then(response => response.json())
            .then(data => {
                console.log('购物车状态:', data);
                if (data.success) {
                    console.log('购物车商品数量:', data.diagnostics.cartItemsCount);
                    console.log('购物车商品列表:', data.diagnostics.cartItems);
                    
                    // 在页面上显示诊断信息
                    showDiagnosticInfo('购物车状态', data);
                } else {
                    console.error('获取购物车状态失败:', data.message);
                }
            })
            .catch(error => {
                console.error('获取购物车状态失败:', error);
            });
    }
    
    // 测试添加商品到购物车
    function testAddToCart() {
        console.log('正在添加测试商品到购物车...');
        fetch('/cart/debug/test-add')
            .then(response => response.json())
            .then(data => {
                console.log('添加测试商品结果:', data);
                if (data.success) {
                    console.log('添加后的购物车商品:', data.cartItems);
                    
                    // 在页面上显示诊断信息
                    showDiagnosticInfo('添加测试商品', data);
                    
                    // 添加成功后检查状态
                    setTimeout(testCartStatus, 1000);
                    
                    // 如果当前是空购物车页面，添加成功后刷新页面
                    if (document.querySelector('.empty-state')) {
                        setTimeout(() => {
                            window.location.reload();
                        }, 2000);
                    }
                } else {
                    console.error('添加测试商品失败:', data.message);
                }
            })
            .catch(error => {
                console.error('添加测试商品失败:', error);
            });
    }
    
    // 显示诊断信息
    function showDiagnosticInfo(title, data) {
        // 创建或获取诊断容器
        let diagContainer = document.getElementById('cart-diagnostic-container');
        if (!diagContainer) {
            diagContainer = document.createElement('div');
            diagContainer.id = 'cart-diagnostic-container';
            diagContainer.className = 'position-fixed top-0 end-0 p-3';
            diagContainer.style.zIndex = '1050';
            diagContainer.style.maxWidth = '400px';
            diagContainer.style.maxHeight = '80vh';
            diagContainer.style.overflow = 'auto';
            document.body.appendChild(diagContainer);
        }
        
        // 创建诊断卡片
        const diagId = 'diag-' + Date.now();
        const diagCard = document.createElement('div');
        diagCard.id = diagId;
        diagCard.className = 'card mb-3 shadow-sm';
        
        // 卡片头部
        const cardHeader = document.createElement('div');
        cardHeader.className = 'card-header d-flex justify-content-between align-items-center bg-light';
        cardHeader.innerHTML = `
            <h6 class="mb-0">${title}</h6>
            <button type="button" class="btn-close" aria-label="Close"></button>
        `;
        
        // 卡片内容
        const cardBody = document.createElement('div');
        cardBody.className = 'card-body';
        
        // 创建JSON预格式化显示
        const pre = document.createElement('pre');
        pre.className = 'mb-0';
        pre.style.fontSize = '0.8rem';
        pre.style.maxHeight = '300px';
        pre.style.overflow = 'auto';
        pre.textContent = JSON.stringify(data, null, 2);
        
        cardBody.appendChild(pre);
        diagCard.appendChild(cardHeader);
        diagCard.appendChild(cardBody);
        
        // 添加到容器
        diagContainer.appendChild(diagCard);
        
        // 添加关闭按钮事件
        const closeBtn = cardHeader.querySelector('.btn-close');
        closeBtn.addEventListener('click', () => {
            diagContainer.removeChild(diagCard);
        });
        
        // 自动关闭
        setTimeout(() => {
            if (diagContainer.contains(diagCard)) {
                diagContainer.removeChild(diagCard);
            }
        }, 30000);
    }
    
    // 在控制台提供测试函数
    window.testCart = {
        status: testCartStatus,
        add: testAddToCart
    };
    
    // 自动执行测试
    if (document.querySelector('.empty-state')) {
        console.log('检测到购物车为空，可以运行 window.testCart.add() 添加测试商品');
    } else {
        console.log('购物车有商品，可以运行 window.testCart.status() 检查购物车状态');
    }
})(); 