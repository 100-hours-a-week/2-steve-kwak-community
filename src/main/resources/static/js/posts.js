// API 요청에 JWT 토큰을 자동으로 추가하는 함수
async function apiFetch(url, options = {}) {
    const token = localStorage.getItem("token");

    if (!token) {
        throw new Error("로그인 토큰이 필요합니다.");
    }

    const headers = {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
    };


    const response = await fetch(url, {
        headers: headers
    });



    if (!response.ok) {
        throw new Error(`API 요청 실패: ${response.status}`);
    }

    const data = await response.json();
    return data;
}


// DOM이 로드된 후 실행될 코드
document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");

    if (!token) {
        alert("로그인이 필요합니다.");
        window.location.href = "/users/login";
        return;
    }
    await fetchPosts();
});

// 게시글 목록 가져오기
async function fetchPosts() {
    try {
        const posts = await apiFetch("/posts/api");
        renderPosts(posts);
    } catch (error) {
        alert("게시글을 불러오는 중 오류가 발생했습니다.");
    }
}

// 게시글 렌더링
function renderPosts(posts) {
    const postList = document.getElementById("post-list");
    postList.innerHTML = "";

    if (posts.length === 0) {
        postList.innerHTML = "<p>게시글이 없습니다.</p>";
        return;
    }

    posts.forEach(post => {
        if (!post.id) {
            return;
        }


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
                alert("게시글 ID가 없습니다.");
                return;
            }
            window.location.href = `/posts/api1/${post.id}`;
        });

        postList.appendChild(postItem);
    });
}
