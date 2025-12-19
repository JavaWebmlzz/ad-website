/**
 * API测试页面主逻辑
 */

// 配置
const CONFIG = {
    API_BASE: 'http://localhost:8080/api/ads',
    CURRENT_AD: null
};

// DOM元素
const elements = {
    // 控制按钮
    getAdBtn: document.getElementById('getAdBtn'),
    getNewAdBtn: document.getElementById('getNewAdBtn'),
    recordClickBtn: document.getElementById('recordClickBtn'),
    copyAdUrlBtn: document.getElementById('copyAdUrlBtn'),

    // 显示区域
    adDisplayContainer: document.getElementById('adDisplayContainer'),
    emptyState: document.getElementById('emptyState'),
    messageContainer: document.getElementById('messageContainer'),

    // 广告内容元素
    adTitle: document.getElementById('adTitle'),
    adDescription: document.getElementById('adDescription'),
    adVideo: document.getElementById('adVideo'),
    adDuration: document.getElementById('adDuration'),

    // 统计元素
    viewsCount: document.getElementById('viewsCount'),
    clicksCount: document.getElementById('clicksCount'),
    durationValue: document.getElementById('durationValue'),
    adId: document.getElementById('adId')
};

// 工具函数
const utils = {
    // 显示消息
    showMessage: (text, type = 'info') => {
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${type}`;
        messageDiv.innerHTML = `
            <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'}"></i>
            ${text}
        `;

        elements.messageContainer.appendChild(messageDiv);

        // 3秒后自动移除
        setTimeout(() => {
            messageDiv.remove();
        }, 3000);
    },

    // 设置按钮加载状态
    setButtonLoading: (button, isLoading) => {
        if (isLoading) {
            button.dataset.originalText = button.innerHTML;
            button.innerHTML = '<div class="loading"></div> 加载中...';
            button.disabled = true;
        } else {
            if (button.dataset.originalText) {
                button.innerHTML = button.dataset.originalText;
                delete button.dataset.originalText;
            }
            button.disabled = false;
        }
    },

    // 格式化数字
    formatNumber: (num) => {
        return num?.toLocaleString() || '0';
    }
};

// API功能
const apiTester = {
    // 获取随机广告
    getRandomAd: async () => {
        utils.setButtonLoading(elements.getAdBtn, true);

        try {
            const response = await fetch(`${CONFIG.API_BASE}/random`);

            if (response.status === 204) {
                elements.adDisplayContainer.style.display = 'none';
                elements.emptyState.style.display = 'block';
                utils.showMessage('广告服务器暂无可用广告', 'info');
                return null;
            }

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            const ad = await response.json();
            CONFIG.CURRENT_AD = ad;

            // 记录观看
            await apiTester.recordView(ad.id);

            // 显示广告
            apiTester.displayAd(ad);

            utils.showMessage('广告加载成功！', 'success');
            return ad;

        } catch (error) {
            console.error('获取广告失败:', error);
            utils.showMessage(`获取广告失败: ${error.message}`, 'error');
            return null;
        } finally {
            utils.setButtonLoading(elements.getAdBtn, false);
        }
    },

    // 记录观看
    recordView: async (adId) => {
        try {
            await fetch(`${CONFIG.API_BASE}/${adId}/view`, {
                method: 'POST'
            });
        } catch (error) {
            console.error('记录观看失败:', error);
        }
    },

    // 记录点击
    recordClick: async () => {
        if (!CONFIG.CURRENT_AD) {
            utils.showMessage('请先获取一个广告', 'error');
            return;
        }

        utils.setButtonLoading(elements.recordClickBtn, true);

        try {
            const response = await fetch(`${CONFIG.CURRENT_AD.id}/click`, {
                method: 'POST'
            });

            if (response.ok) {
                // 更新本地计数
                CONFIG.CURRENT_AD.clicks = (CONFIG.CURRENT_AD.clicks || 0) + 1;
                elements.clicksCount.textContent = utils.formatNumber(CONFIG.CURRENT_AD.clicks);

                utils.showMessage('点击已记录！', 'success');

                // 模拟点击效果
                elements.recordClickBtn.style.transform = 'scale(0.95)';
                setTimeout(() => {
                    elements.recordClickBtn.style.transform = '';
                }, 200);
            }
        } catch (error) {
            console.error('记录点击失败:', error);
            utils.showMessage('记录点击失败', 'error');
        } finally {
            utils.setButtonLoading(elements.recordClickBtn, false);
        }
    },

    // 显示广告
    displayAd: (ad) => {
        // 隐藏空状态，显示广告
        elements.emptyState.style.display = 'none';
        elements.adDisplayContainer.style.display = 'block';

        // 更新广告内容
        elements.adTitle.textContent = ad.title;
        elements.adDescription.textContent = ad.description || '暂无描述';

        // 更新视频
        elements.adVideo.src = ad.videoUrl;
        elements.adVideo.load();

        // 更新统计信息
        elements.viewsCount.textContent = utils.formatNumber(ad.views);
        elements.clicksCount.textContent = utils.formatNumber(ad.clicks);
        elements.durationValue.textContent = ad.duration || 15;
        elements.adDuration.textContent = `${ad.duration || 15}秒`;
        elements.adId.textContent = ad.id;

        // 视频播放时自动记录观看
        elements.adVideo.addEventListener('play', () => {
            console.log(`广告 ${ad.id} 开始播放`);
        });
    },

    // 复制广告链接
    copyAdUrl: () => {
        if (!CONFIG.CURRENT_AD) {
            utils.showMessage('请先获取一个广告', 'error');
            return;
        }

        const adUrl = CONFIG.CURRENT_AD.videoUrl;
        navigator.clipboard.writeText(adUrl).then(() => {
            utils.showMessage('广告链接已复制到剪贴板', 'success');
        }).catch(() => {
            // 降级方案
            const textArea = document.createElement('textarea');
            textArea.value = adUrl;
            document.body.appendChild(textArea);
            textArea.select();
            document.execCommand('copy');
            document.body.removeChild(textArea);
            utils.showMessage('广告链接已复制', 'info');
        });
    },

    // 获取新广告
    getNewAd: async () => {
        await apiTester.getRandomAd();
    }
};

// 事件监听器
const setupEventListeners = () => {
    // 获取随机广告按钮
    elements.getAdBtn.addEventListener('click', () => {
        apiTester.getRandomAd();
    });

    // 获取新广告按钮
    elements.getNewAdBtn.addEventListener('click', () => {
        apiTester.getNewAd();
    });

    // 记录点击按钮
    elements.recordClickBtn.addEventListener('click', () => {
        apiTester.recordClick();
    });

    // 复制广告链接按钮
    elements.copyAdUrlBtn.addEventListener('click', () => {
        apiTester.copyAdUrl();
    });

    // 视频播放结束事件
    elements.adVideo.addEventListener('ended', () => {
        if (CONFIG.CURRENT_AD) {
            console.log(`广告 ${CONFIG.CURRENT_AD.id} 播放结束`);
        }
    });

    // 视频错误处理
    elements.adVideo.addEventListener('error', () => {
        utils.showMessage('视频加载失败，请检查网络连接', 'error');
    });
};

// 页面加载完成时初始化
document.addEventListener('DOMContentLoaded', () => {
    setupEventListeners();

    // 检查API连通性
    checkApiConnectivity();
});

// 检查API连通性
async function checkApiConnectivity() {
    try {
        const response = await fetch(CONFIG.API_BASE + '/random');
        if (response.ok || response.status === 204) {
            console.log('API服务器连接正常');
        }
    } catch (error) {
        console.warn('无法连接到API服务器:', error);
        utils.showMessage('无法连接到广告服务器，请确保后端服务已启动', 'error');
    }
}

// 导出到全局作用域
window.apiTester = apiTester;
window.utils = utils;