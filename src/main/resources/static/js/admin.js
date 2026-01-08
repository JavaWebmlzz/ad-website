/**
 * 广告管理后台主逻辑
 */

// 配置
const CONFIG = {
    //API_BASE: 'http://localhost:8080/api/admin/ads',
    API_BASE: '/api/admin/ads',
    MAX_FILE_SIZE: 50 * 1024 * 1024, // 50MB
    ALLOWED_VIDEO_TYPES: ['video/mp4', 'video/webm', 'video/ogg']
};

// DOM元素
const elements = {
    adForm: document.getElementById('adForm'),
    titleInput: document.getElementById('title'),
    descriptionInput: document.getElementById('description'),
    videoFileInput: document.getElementById('videoFile'),
    durationInput: document.getElementById('duration'),
    adList: document.getElementById('adList'),
    loadingIndicator: document.getElementById('loadingIndicator'),
    emptyState: document.getElementById('emptyState'),
    refreshBtn: document.getElementById('refreshBtn'),
    messageContainer: document.getElementById('messageContainer')
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

    // 验证文件
    validateFile: (file) => {
        if (file.size > CONFIG.MAX_FILE_SIZE) {
            return `文件大小不能超过 ${CONFIG.MAX_FILE_SIZE / (1024 * 1024)}MB`;
        }

        if (!CONFIG.ALLOWED_VIDEO_TYPES.includes(file.type)) {
            return '仅支持 MP4、WebM、OGG 格式的视频文件';
        }

        return null;
    },

    // 格式化时间
    formatDateTime: (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    },

    // 设置加载状态
    setLoading: (isLoading) => {
        elements.loadingIndicator.style.display = isLoading ? 'block' : 'none';
        if (isLoading) {
            elements.adList.innerHTML = '';
        }
    }
};

// 广告管理功能
const adManager = {
    // 加载所有广告
    loadAds: async () => {
        utils.setLoading(true);

        try {
            const response = await fetch(CONFIG.API_BASE);

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            const ads = await response.json();
            adManager.displayAds(ads);

        } catch (error) {
            console.error('加载广告失败:', error);
            utils.showMessage(`加载失败: ${error.message}`, 'error');
        } finally {
            utils.setLoading(false);
        }
    },

    // 显示广告列表
    displayAds: (ads) => {
        if (ads.length === 0) {
            elements.emptyState.style.display = 'block';
            elements.adList.innerHTML = '';
            return;
        }

        elements.emptyState.style.display = 'none';

        const adsHTML = ads.map(ad => `
            <div class="ad-item" data-id="${ad.id}">
                <div style="display: flex; justify-content: space-between; align-items: start;">
                    <h3>${ad.title}</h3>
                    <span class="status-badge ${ad.isActive ? 'status-active' : 'status-inactive'}">
                        ${ad.isActive ? '活跃' : '禁用'}
                    </span>
                </div>
                
                <p>${ad.description || '暂无描述'}</p>
                
                <div class="ad-stats">
                    <div class="stat-item">
                        <i class="fas fa-eye"></i>
                        <span>观看: ${ad.views || 0}</span>
                    </div>
                    <div class="stat-item">
                        <i class="fas fa-mouse-pointer"></i>
                        <span>点击: ${ad.clicks || 0}</span>
                    </div>
                    <div class="stat-item">
                        <i class="fas fa-clock"></i>
                        <span>时长: ${ad.duration || 15}秒</span>
                    </div>
                    <div class="stat-item">
                        <i class="fas fa-calendar-alt"></i>
                        <span>创建: ${utils.formatDateTime(ad.createdAt)}</span>
                    </div>
                </div>
                
                <div class="video-preview">
                    <video controls width="400">
<!--                        <source src="http://localhost:8080${ad.videoUrl}" type="video/mp4">-->
                        <source src="${ad.videoUrl}" type="video/mp4">
                        您的浏览器不支持视频标签
                    </video>
                </div>
                
                <div class="action-buttons">
                    <button class="btn btn-danger btn-sm" onclick="adManager.deleteAd(${ad.id})">
                        <i class="fas fa-trash-alt"></i> 删除广告
                    </button>
                    <button class="btn btn-primary btn-sm" onclick="adManager.toggleAdStatus(${ad.id}, ${!ad.isActive})">
                        <i class="fas fa-power-off"></i> ${ad.isActive ? '禁用' : '启用'}
                    </button>
                    <button class="btn btn-primary btn-sm" onclick="adManager.copyApiUrl(${ad.id})">
                        <i class="fas fa-link"></i> 复制API链接
                    </button>
                </div>
            </div>
        `).join('');

        elements.adList.innerHTML = adsHTML;
    },

    // 上传广告
    uploadAd: async (formData) => {
        const submitBtn = elements.adForm.querySelector('button[type="submit"]');
        const originalText = submitBtn.innerHTML;

        try {
            // 显示加载状态
            submitBtn.innerHTML = '<div class="loading"></div> 上传中...';
            submitBtn.disabled = true;

            const response = await fetch(CONFIG.API_BASE, {
                method: 'POST',
                body: formData
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `上传失败: ${response.status}`);
            }

            const result = await response.json();
            utils.showMessage('广告上传成功！', 'success');
            adManager.loadAds();
            elements.adForm.reset();

        } catch (error) {
            console.error('上传失败:', error);
            utils.showMessage(`上传失败: ${error.message}`, 'error');
        } finally {
            // 恢复按钮状态
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
        }
    },

    // 删除广告
    deleteAd: async (id) => {
        if (!confirm('确定要删除这个广告吗？此操作不可撤销。')) {
            return;
        }

        try {
            const response = await fetch(`${CONFIG.API_BASE}/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error(`删除失败: ${response.status}`);
            }

            utils.showMessage('广告删除成功！', 'success');
            adManager.loadAds();

        } catch (error) {
            console.error('删除失败:', error);
            utils.showMessage('删除失败，请重试', 'error');
        }
    },

    // 切换广告状态
    toggleAdStatus: async (id, newStatus) => {
        try {
            const ad = await fetch(`${CONFIG.API_BASE}/${id}`).then(res => res.json());
            ad.isActive = newStatus;

            const response = await fetch(`${CONFIG.API_BASE}/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(ad)
            });

            if (!response.ok) {
                throw new Error(`更新失败: ${response.status}`);
            }

            utils.showMessage(`广告已${newStatus ? '启用' : '禁用'}`, 'success');
            adManager.loadAds();

        } catch (error) {
            console.error('状态更新失败:', error);
            utils.showMessage('状态更新失败', 'error');
        }
    },

    // 复制API链接
    // copyApiUrl: (id) => {
    //     // const apiUrl = `http://localhost:8080/api/ads/random`;
    //     const apiUrl = `/api/ads/random`;
    //     navigator.clipboard.writeText(apiUrl).then(() => {
    //         utils.showMessage('API链接已复制到剪贴板', 'success');
    //     }).catch(() => {
    //         // 降级方案
    //         const textArea = document.createElement('textarea');
    //         textArea.value = apiUrl;
    //         document.body.appendChild(textArea);
    //         textArea.select();
    //         document.execCommand('copy');
    //         document.body.removeChild(textArea);
    //         utils.showMessage('API链接已复制', 'info');
    //     });
    // }
    copyApiUrl(id) {   // 把 id 当参数传进来
        // const apiUrl = `${location.origin}/api/ads/${id}`;
        // // 下面降级复制逻辑完全不变
        // const textArea = document.createElement('textarea');
        // textArea.value = apiUrl;
        // textArea.style.position = 'fixed';
        // textArea.style.left = '-9999px';
        // document.body.appendChild(textArea);
        // textArea.focus();
        // textArea.select();
        // try {
        //     const ok = document.execCommand('copy');
        //     utils.showMessage(ok ? 'API链接已复制' : '复制失败，请手动复制',
        //         ok ? 'info' : 'warning');
        // } catch (err) {
        //     utils.showMessage('复制失败，请手动复制', 'warning');
        // } finally {
        //     document.body.removeChild(textArea);
        // }

        // fetch(`/api/ads/${id}`)
        //     .then(res => res.json())
        //     .then(ad => {
        //         const videoUrl = ad.videoFullUrl;   // 真正的播放地址
        //         // 下面降级复制代码完全不变
        //         const textArea = document.createElement('textarea');
        //         textArea.value = videoUrl;
        //         textArea.style.position = 'fixed';
        //         textArea.style.left = '-9999px';
        //         document.body.appendChild(textArea);
        //         textArea.focus();
        //         textArea.select();
        //         try {
        //             const ok = document.execCommand('copy');
        //             utils.showMessage(ok ? '视频链接已复制' : '复制失败，请手动复制',
        //                 ok ? 'info' : 'warning');
        //         } catch (err) {
        //             utils.showMessage('复制失败，请手动复制', 'warning');
        //         } finally {
        //             document.body.removeChild(textArea);
        //         }
        //     })
        //     .catch(() => utils.showMessage('获取地址失败', 'error'));

        // 1. 先向后端要完整视频地址
        axios.get(`/api/ads/${id}`)
            .then(res => {
                const videoUrl = res.data.videoFullUrl; // 真正的播放地址
                // 2. 降级复制
                const textArea = document.createElement('textarea');
                textArea.value = videoUrl;
                textArea.style.position = 'fixed';
                textArea.style.left = '-9999px';
                document.body.appendChild(textArea);
                textArea.focus();
                textArea.select();
                try {
                    const ok = document.execCommand('copy');
                    utils.showMessage(ok ? '视频链接已复制' : '复制失败，请手动复制',
                        ok ? 'info' : 'warning');
                } catch (err) {
                    utils.showMessage('复制失败，请手动复制', 'warning');
                } finally {
                    document.body.removeChild(textArea);
                }
            })
            .catch(() => utils.showMessage('获取地址失败', 'error'));

    }

};


// 事件监听器
const setupEventListeners = () => {
    // 表单提交
    elements.adForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        // 验证表单
        if (!elements.titleInput.value.trim()) {
            utils.showMessage('请输入广告标题', 'error');
            return;
        }

        const file = elements.videoFileInput.files[0];
        if (!file) {
            utils.showMessage('请选择视频文件', 'error');
            return;
        }

        // 验证文件
        const fileError = utils.validateFile(file);
        if (fileError) {
            utils.showMessage(fileError, 'error');
            return;
        }

        // 准备表单数据
        const formData = new FormData();
        formData.append('title', elements.titleInput.value.trim());
        formData.append('description', elements.descriptionInput.value.trim());
        formData.append('videoFile', file);
        formData.append('duration', elements.durationInput.value);

        // 上传广告
        await adManager.uploadAd(formData);
    });

    // 刷新按钮
    elements.refreshBtn.addEventListener('click', () => {
        adManager.loadAds();
    });

    // 文件选择变化时显示文件名
    elements.videoFileInput.addEventListener('change', function() {
        const fileName = this.files[0]?.name || '未选择文件';
        const fileInfo = document.createElement('small');
        fileInfo.style.display = 'block';
        fileInfo.style.marginTop = '5px';
        fileInfo.style.color = '#28a745';
        fileInfo.innerHTML = `<i class="fas fa-file-video"></i> ${fileName}`;

        // 移除之前的文件信息
        const oldInfo = this.parentNode.querySelector('small:last-child');
        if (oldInfo && oldInfo !== this.parentNode.querySelector('small:first-child')) {
            oldInfo.remove();
        }

        if (this.files[0]) {
            this.parentNode.appendChild(fileInfo);
        }
    });
};

// 页面加载完成时初始化
document.addEventListener('DOMContentLoaded', () => {
    setupEventListeners();
    adManager.loadAds();
});

// 导出到全局作用域
window.adManager = adManager;
window.utils = utils;