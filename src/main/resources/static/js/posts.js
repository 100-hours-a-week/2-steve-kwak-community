// API 요청에 JWT 토큰을 자동으로 추가하는 함수
async function apiFetch(url, options = {}) {
    const token = localStorage.getItem("token");

    if (!token) {
        console.error("❌ 로그인 토큰이 없습니다.");
        throw new Error("로그인 토큰이 필요합니다.");
    }

    const headers = {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
    };

    console.log("📡 API 요청 URL:", url);
    console.log("📢 요청 헤더:", headers);

    const response = await fetch(url, {
        headers: headers
    });

    console.log("🔄 API 응답 상태 코드:", response.status);

    if (!response.ok) {
        console.error(`❌ API 요청 실패: ${response.status}`);
        throw new Error(`API 요청 실패: ${response.status}`);
    }

    const data = await response.json();
    console.log("✅ API 응답 데이터:", data);
    return data;
}


// DOM이 로드된 후 실행될 코드
document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");

    if (!token) {
        console.warn("⚠️ 로그인이 필요합니다.");
        alert("로그인이 필요합니다.");
        window.location.href = "/users/login";
        return;
    }

    console.log("🔑 저장된 토큰:", token); // 토큰 확인

    await fetchPosts();
});

// 게시글 목록 가져오기
async function fetchPosts() {
    try {
        console.log("📥 게시글 목록 요청 시작");
        const posts = await apiFetch("/posts/api");
        console.log("📃 가져온 게시글 목록:", posts);
        renderPosts(posts);
    } catch (error) {
        console.error("❌ 게시글 불러오기 오류:", error);
        alert("게시글을 불러오는 중 오류가 발생했습니다.");
    }
}

// 게시글 렌더링
function renderPosts(posts) {
    const postList = document.getElementById("post-list");
    postList.innerHTML = "";

    if (posts.length === 0) {
        console.warn("⚠️ 게시글이 없습니다.");
        postList.innerHTML = "<p>게시글이 없습니다.</p>";
        return;
    }

    posts.forEach(post => {
        if (!post.id) {
            console.warn("⚠️ 게시글 ID가 없습니다. 건너뜁니다.", post);
            return;
        }

        console.log(`📝 게시글 렌더링: ${post.title} (ID: ${post.id})`);

        const postItem = document.createElement("div");
        postItem.classList.add("post-item");
        postItem.innerHTML = `
            <div class="post-header">
                <h3 class="post-title">${post.title}</h3>
                <p class="post-author">${post.author}</p> 
            </div>
            <div class="post-meta">
                <p class="post-date">${new Date(post.createdAt).toLocaleString()}</p>
                <div class="post-stats">
                    <p class="post-likes">👍 ${post.likeCount}</p>
                    <p class="post-comments">💬 ${post.commentCount}</p>
                    <p class="post-views">👁️ ${post.viewCount}</p>
                </div>
            </div>
        `;

        postItem.addEventListener("click", () => {
            if (!post.id) {
                console.error("❌ 게시글 ID가 없습니다.");
                alert("게시글 ID가 없습니다.");
                return;
            }
            console.log(`➡️ 게시글 이동: /posts/api1/${post.id}`);
            window.location.href = `/posts/api1/${post.id}`;
        });

        postList.appendChild(postItem);
    });
}
