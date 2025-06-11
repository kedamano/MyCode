/**
 * 统计报表图表脚本
 */

// 图表配置
const chartConfig = {
    // 颜色方案
    colors: ['#3498db', '#2ecc71', '#f39c12', '#e74c3c', '#9b59b6', '#1abc9c', '#34495e', '#f1c40f'],
    
    // 暗色模式下的颜色方案
    darkColors: ['#4dabf7', '#51cf66', '#fcc419', '#ff6b6b', '#cc5de8', '#38d9a9', '#adb5bd', '#ffd43b'],
    
    // 获取当前主题的颜色方案
    getColors: function() {
        return document.body.classList.contains('dark-mode') ? this.darkColors : this.colors;
    },
    
    // 通用图表配置
    common: {
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        }
    }
};

// 销售趋势图
function renderSalesChart(data, type = 'daily') {
    const salesChart = echarts.init(document.getElementById('salesChart'));
    
    const dates = data.map(item => item.date);
    const sales = data.map(item => item.amount);
    const orders = data.map(item => item.count);
    
    const colors = chartConfig.getColors();
    
    const option = {
        ...chartConfig.common,
        title: {
            text: '销售趋势',
            left: 'center',
            textStyle: {
                color: getComputedStyle(document.body).getPropertyValue('--text-color')
            }
        },
        legend: {
            data: ['销售额', '订单数'],
            bottom: 10,
            textStyle: {
                color: getComputedStyle(document.body).getPropertyValue('--text-color')
            }
        },
        xAxis: {
            type: 'category',
            data: dates,
            axisLine: {
                lineStyle: {
                    color: getComputedStyle(document.body).getPropertyValue('--text-light')
                }
            },
            axisLabel: {
                color: getComputedStyle(document.body).getPropertyValue('--text-color')
            }
        },
        yAxis: [
            {
                type: 'value',
                name: '销售额',
                axisLabel: {
                    formatter: '{value} 元',
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                },
                axisLine: {
                    lineStyle: {
                        color: getComputedStyle(document.body).getPropertyValue('--text-light')
                    }
                }
            },
            {
                type: 'value',
                name: '订单数',
                axisLabel: {
                    formatter: '{value} 单',
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                },
                axisLine: {
                    lineStyle: {
                        color: getComputedStyle(document.body).getPropertyValue('--text-light')
                    }
                }
            }
        ],
        series: [
            {
                name: '销售额',
                type: 'bar',
                data: sales,
                itemStyle: {
                    color: colors[0]
                }
            },
            {
                name: '订单数',
                type: 'line',
                yAxisIndex: 1,
                data: orders,
                itemStyle: {
                    color: colors[1]
                },
                lineStyle: {
                    width: 3
                },
                symbol: 'circle',
                symbolSize: 8
            }
        ]
    };
    
    salesChart.setOption(option);
    
    // 监听窗口大小变化，调整图表大小
    window.addEventListener('resize', function() {
        salesChart.resize();
    });
    
    // 监听主题变化
    document.addEventListener('themeChanged', function() {
        salesChart.setOption({
            title: {
                textStyle: {
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                }
            },
            legend: {
                textStyle: {
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                }
            },
            xAxis: {
                axisLine: {
                    lineStyle: {
                        color: getComputedStyle(document.body).getPropertyValue('--text-light')
                    }
                },
                axisLabel: {
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                }
            },
            yAxis: [
                {
                    axisLabel: {
                        color: getComputedStyle(document.body).getPropertyValue('--text-color')
                    },
                    axisLine: {
                        lineStyle: {
                            color: getComputedStyle(document.body).getPropertyValue('--text-light')
                        }
                    }
                },
                {
                    axisLabel: {
                        color: getComputedStyle(document.body).getPropertyValue('--text-color')
                    },
                    axisLine: {
                        lineStyle: {
                            color: getComputedStyle(document.body).getPropertyValue('--text-light')
                        }
                    }
                }
            ],
            series: [
                {
                    itemStyle: {
                        color: chartConfig.getColors()[0]
                    }
                },
                {
                    itemStyle: {
                        color: chartConfig.getColors()[1]
                    }
                }
            ]
        });
    });
}

// 农户销售排行图
function renderFarmerChart(data) {
    const farmerChart = echarts.init(document.getElementById('farmerChart'));
    
    const farmers = data.map(item => item.farmerName);
    const sales = data.map(item => item.totalSales);
    
    const colors = chartConfig.getColors();
    
    const option = {
        ...chartConfig.common,
        title: {
            text: '农户销售排行',
            left: 'center',
            textStyle: {
                color: getComputedStyle(document.body).getPropertyValue('--text-color')
            }
        },
        xAxis: {
            type: 'category',
            data: farmers,
            axisLabel: {
                interval: 0,
                rotate: 30,
                color: getComputedStyle(document.body).getPropertyValue('--text-color')
            },
            axisLine: {
                lineStyle: {
                    color: getComputedStyle(document.body).getPropertyValue('--text-light')
                }
            }
        },
        yAxis: {
            type: 'value',
            name: '销售额',
            axisLabel: {
                formatter: '{value} 元',
                color: getComputedStyle(document.body).getPropertyValue('--text-color')
            },
            axisLine: {
                lineStyle: {
                    color: getComputedStyle(document.body).getPropertyValue('--text-light')
                }
            }
        },
        series: [
            {
                name: '销售额',
                type: 'bar',
                data: sales,
                itemStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: colors[0] },
                        { offset: 1, color: colors[1] }
                    ])
                },
                label: {
                    show: true,
                    position: 'top',
                    formatter: '{c} 元',
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                }
            }
        ]
    };
    
    farmerChart.setOption(option);
    
    // 监听窗口大小变化
    window.addEventListener('resize', function() {
        farmerChart.resize();
    });
    
    // 监听主题变化
    document.addEventListener('themeChanged', function() {
        farmerChart.setOption({
            title: {
                textStyle: {
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                }
            },
            xAxis: {
                axisLabel: {
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                },
                axisLine: {
                    lineStyle: {
                        color: getComputedStyle(document.body).getPropertyValue('--text-light')
                    }
                }
            },
            yAxis: {
                axisLabel: {
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                },
                axisLine: {
                    lineStyle: {
                        color: getComputedStyle(document.body).getPropertyValue('--text-light')
                    }
                }
            },
            series: [
                {
                    itemStyle: {
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                            { offset: 0, color: chartConfig.getColors()[0] },
                            { offset: 1, color: chartConfig.getColors()[1] }
                        ])
                    },
                    label: {
                        color: getComputedStyle(document.body).getPropertyValue('--text-color')
                    }
                }
            ]
        });
    });
}

// 商品销售排行图
function renderProductChart(data) {
    const productChart = echarts.init(document.getElementById('productChart'));
    
    const products = data.map(item => item.productName);
    const sales = data.map(item => item.totalSales);
    const quantities = data.map(item => item.quantity);
    
    const colors = chartConfig.getColors();
    
    const option = {
        ...chartConfig.common,
        title: {
            text: '商品销售排行',
            left: 'center',
            textStyle: {
                color: getComputedStyle(document.body).getPropertyValue('--text-color')
            }
        },
        legend: {
            data: ['销售额', '销售量'],
            bottom: 10,
            textStyle: {
                color: getComputedStyle(document.body).getPropertyValue('--text-color')
            }
        },
        xAxis: {
            type: 'category',
            data: products,
            axisLabel: {
                interval: 0,
                rotate: 30,
                color: getComputedStyle(document.body).getPropertyValue('--text-color')
            },
            axisLine: {
                lineStyle: {
                    color: getComputedStyle(document.body).getPropertyValue('--text-light')
                }
            }
        },
        yAxis: [
            {
                type: 'value',
                name: '销售额',
                axisLabel: {
                    formatter: '{value} 元',
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                },
                axisLine: {
                    lineStyle: {
                        color: getComputedStyle(document.body).getPropertyValue('--text-light')
                    }
                }
            },
            {
                type: 'value',
                name: '销售量',
                axisLabel: {
                    formatter: '{value} 件',
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                },
                axisLine: {
                    lineStyle: {
                        color: getComputedStyle(document.body).getPropertyValue('--text-light')
                    }
                }
            }
        ],
        series: [
            {
                name: '销售额',
                type: 'bar',
                data: sales,
                itemStyle: {
                    color: colors[2]
                }
            },
            {
                name: '销售量',
                type: 'bar',
                yAxisIndex: 1,
                data: quantities,
                itemStyle: {
                    color: colors[3]
                }
            }
        ]
    };
    
    productChart.setOption(option);
    
    // 监听窗口大小变化
    window.addEventListener('resize', function() {
        productChart.resize();
    });
    
    // 监听主题变化
    document.addEventListener('themeChanged', function() {
        productChart.setOption({
            title: {
                textStyle: {
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                }
            },
            legend: {
                textStyle: {
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                }
            },
            xAxis: {
                axisLabel: {
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                },
                axisLine: {
                    lineStyle: {
                        color: getComputedStyle(document.body).getPropertyValue('--text-light')
                    }
                }
            },
            yAxis: [
                {
                    axisLabel: {
                        color: getComputedStyle(document.body).getPropertyValue('--text-color')
                    },
                    axisLine: {
                        lineStyle: {
                            color: getComputedStyle(document.body).getPropertyValue('--text-light')
                        }
                    }
                },
                {
                    axisLabel: {
                        color: getComputedStyle(document.body).getPropertyValue('--text-color')
                    },
                    axisLine: {
                        lineStyle: {
                            color: getComputedStyle(document.body).getPropertyValue('--text-light')
                        }
                    }
                }
            ],
            series: [
                {
                    itemStyle: {
                        color: chartConfig.getColors()[2]
                    }
                },
                {
                    itemStyle: {
                        color: chartConfig.getColors()[3]
                    }
                }
            ]
        });
    });
}

// 分类销售占比图
function renderCategoryChart(data) {
    const categoryChart = echarts.init(document.getElementById('categoryChart'));
    
    const categories = data.map(item => item.category);
    const sales = data.map(item => item.totalSales);
    
    const colors = chartConfig.getColors();
    
    const option = {
        tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        title: {
            text: '分类销售占比',
            left: 'center',
            textStyle: {
                color: getComputedStyle(document.body).getPropertyValue('--text-color')
            }
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            data: categories,
            textStyle: {
                color: getComputedStyle(document.body).getPropertyValue('--text-color')
            }
        },
        series: [
            {
                name: '销售额',
                type: 'pie',
                radius: ['40%', '70%'],
                avoidLabelOverlap: false,
                itemStyle: {
                    borderRadius: 10,
                    borderColor: getComputedStyle(document.body).getPropertyValue('--bg-color'),
                    borderWidth: 2
                },
                label: {
                    show: true,
                    formatter: '{b}: {c} 元 ({d}%)',
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                },
                emphasis: {
                    label: {
                        show: true,
                        fontSize: '18',
                        fontWeight: 'bold'
                    }
                },
                data: categories.map((category, index) => {
                    return {
                        name: category,
                        value: sales[index],
                        itemStyle: {
                            color: colors[index % colors.length]
                        }
                    };
                })
            }
        ]
    };
    
    categoryChart.setOption(option);
    
    // 监听窗口大小变化
    window.addEventListener('resize', function() {
        categoryChart.resize();
    });
    
    // 监听主题变化
    document.addEventListener('themeChanged', function() {
        categoryChart.setOption({
            title: {
                textStyle: {
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                }
            },
            legend: {
                textStyle: {
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                }
            },
            series: [
                {
                    itemStyle: {
                        borderColor: getComputedStyle(document.body).getPropertyValue('--bg-color')
                    },
                    label: {
                        color: getComputedStyle(document.body).getPropertyValue('--text-color')
                    },
                    data: categories.map((category, index) => {
                        return {
                            name: category,
                            value: sales[index],
                            itemStyle: {
                                color: chartConfig.getColors()[index % chartConfig.getColors().length]
                            }
                        };
                    })
                }
            ]
        });
    });
}

// 职工购买排行图
function renderEmployeeChart(data) {
    const employeeChart = echarts.init(document.getElementById('employeeChart'));
    
    const employees = data.map(item => item.employeeName);
    const purchases = data.map(item => item.totalPurchases);
    const orderCounts = data.map(item => item.orderCount);
    
    const colors = chartConfig.getColors();
    
    const option = {
        ...chartConfig.common,
        title: {
            text: '职工购买排行',
            left: 'center',
            textStyle: {
                color: getComputedStyle(document.body).getPropertyValue('--text-color')
            }
        },
        legend: {
            data: ['购买金额', '订单数量'],
            bottom: 10,
            textStyle: {
                color: getComputedStyle(document.body).getPropertyValue('--text-color')
            }
        },
        xAxis: {
            type: 'category',
            data: employees,
            axisLabel: {
                interval: 0,
                rotate: 30,
                color: getComputedStyle(document.body).getPropertyValue('--text-color')
            },
            axisLine: {
                lineStyle: {
                    color: getComputedStyle(document.body).getPropertyValue('--text-light')
                }
            }
        },
        yAxis: [
            {
                type: 'value',
                name: '购买金额',
                axisLabel: {
                    formatter: '{value} 元',
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                },
                axisLine: {
                    lineStyle: {
                        color: getComputedStyle(document.body).getPropertyValue('--text-light')
                    }
                }
            },
            {
                type: 'value',
                name: '订单数量',
                axisLabel: {
                    formatter: '{value} 单',
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                },
                axisLine: {
                    lineStyle: {
                        color: getComputedStyle(document.body).getPropertyValue('--text-light')
                    }
                }
            }
        ],
        series: [
            {
                name: '购买金额',
                type: 'bar',
                data: purchases,
                itemStyle: {
                    color: colors[4]
                }
            },
            {
                name: '订单数量',
                type: 'line',
                yAxisIndex: 1,
                data: orderCounts,
                itemStyle: {
                    color: colors[5]
                },
                lineStyle: {
                    width: 3
                },
                symbol: 'circle',
                symbolSize: 8
            }
        ]
    };
    
    employeeChart.setOption(option);
    
    // 监听窗口大小变化
    window.addEventListener('resize', function() {
        employeeChart.resize();
    });
    
    // 监听主题变化
    document.addEventListener('themeChanged', function() {
        employeeChart.setOption({
            title: {
                textStyle: {
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                }
            },
            legend: {
                textStyle: {
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                }
            },
            xAxis: {
                axisLabel: {
                    color: getComputedStyle(document.body).getPropertyValue('--text-color')
                },
                axisLine: {
                    lineStyle: {
                        color: getComputedStyle(document.body).getPropertyValue('--text-light')
                    }
                }
            },
            yAxis: [
                {
                    axisLabel: {
                        color: getComputedStyle(document.body).getPropertyValue('--text-color')
                    },
                    axisLine: {
                        lineStyle: {
                            color: getComputedStyle(document.body).getPropertyValue('--text-light')
                        }
                    }
                },
                {
                    axisLabel: {
                        color: getComputedStyle(document.body).getPropertyValue('--text-color')
                    },
                    axisLine: {
                        lineStyle: {
                            color: getComputedStyle(document.body).getPropertyValue('--text-light')
                        }
                    }
                }
            ],
            series: [
                {
                    itemStyle: {
                        color: chartConfig.getColors()[4]
                    }
                },
                {
                    itemStyle: {
                        color: chartConfig.getColors()[5]
                    }
                }
            ]
        });
    });
}

// 加载统计数据
function loadStatisticsData() {
    // 获取时间范围
    const timeRange = document.querySelector('.time-range.active')?.dataset.range || 30;
    
    // 发起AJAX请求获取数据
    fetch(`/admin/statistics/data?timeRange=${timeRange}`)
        .then(response => response.json())
        .then(data => {
            // 渲染各个图表
            renderSalesChart(data.salesTrend);
            renderFarmerChart(data.topFarmers);
            renderProductChart(data.topProducts);
            renderCategoryChart(data.categorySales);
            renderEmployeeChart(data.topEmployees);
        })
        .catch(error => {
            console.error('Error loading statistics data:', error);
            // 如果加载失败，使用模拟数据进行展示
            useMockData();
        });
}

// 使用模拟数据
function useMockData() {
    renderSalesChart(generateMockSalesData());
    renderFarmerChart(generateMockFarmerData());
    renderProductChart(generateMockProductData());
    renderCategoryChart(generateMockCategoryData());
    renderEmployeeChart(generateMockEmployeeData());
}

// 生成模拟销售趋势数据
function generateMockSalesData() {
    const data = [];
    const now = new Date();
    
    for (let i = 29; i >= 0; i--) {
        const date = new Date(now);
        date.setDate(date.getDate() - i);
        
        data.push({
            date: `${date.getMonth() + 1}/${date.getDate()}`,
            amount: Math.floor(Math.random() * 5000) + 1000,
            count: Math.floor(Math.random() * 20) + 5
        });
    }
    
    return data;
}

// 生成模拟农户销售数据
function generateMockFarmerData() {
    const farmers = ['张三农场', '李四果园', '王五蔬菜基地', '赵六家庭农场', '钱七有机农业'];
    
    return farmers.map(name => ({
        farmerName: name,
        totalSales: Math.floor(Math.random() * 10000) + 5000
    }));
}

// 生成模拟商品销售数据
function generateMockProductData() {
    const products = ['有机大米', '新鲜苹果', '生态鸡蛋', '山区土豆', '野生蘑菇'];
    
    return products.map(name => ({
        productName: name,
        totalSales: Math.floor(Math.random() * 8000) + 2000,
        quantity: Math.floor(Math.random() * 500) + 100
    }));
}

// 生成模拟分类销售数据
function generateMockCategoryData() {
    const categories = ['水果', '蔬菜', '粮油', '干货', '其他'];
    
    return categories.map(category => ({
        category: category,
        totalSales: Math.floor(Math.random() * 20000) + 10000
    }));
}

// 生成模拟职工购买数据
function generateMockEmployeeData() {
    const employees = ['张经理', '李主管', '王工程师', '赵分析师', '钱设计师'];
    
    return employees.map(name => ({
        employeeName: name,
        totalPurchases: Math.floor(Math.random() * 3000) + 1000,
        orderCount: Math.floor(Math.random() * 15) + 5
    }));
}

// 页面加载完成后初始化图表
document.addEventListener('DOMContentLoaded', function() {
    // 初始化时间范围选择
    const timeRangeLinks = document.querySelectorAll('.time-range');
    timeRangeLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            
            // 移除其他链接的active类
            timeRangeLinks.forEach(item => item.classList.remove('active'));
            
            // 添加当前链接的active类
            this.classList.add('active');
            
            // 重新加载数据
            loadStatisticsData();
        });
    });
    
    // 初始化销售视图切换
    const salesViewButtons = document.querySelectorAll('.sales-view');
    salesViewButtons.forEach(button => {
        button.addEventListener('click', function() {
            // 移除其他按钮的active类
            salesViewButtons.forEach(item => {
                item.classList.remove('active');
                item.classList.remove('btn-primary');
                item.classList.add('btn-outline-primary');
            });
            
            // 添加当前按钮的active类
            this.classList.add('active');
            this.classList.add('btn-primary');
            this.classList.remove('btn-outline-primary');
            
            // 重新加载销售趋势图
            loadStatisticsData();
        });
    });
    
    // 初始化加载数据
    loadStatisticsData();
    
    // 如果没有数据，使用模拟数据
    if (!document.getElementById('salesChart').getAttribute('_echarts_instance_')) {
        useMockData();
    }
}); 