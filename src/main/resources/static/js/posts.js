document.addEventListener("DOMContentLoaded", async () => {
    const userId = localStorage.getItem("user_id");
    if (!userId) {
        alert("로그인이 필요합니다.");
        window.location.href = "/users/login"; // 로그인 페이지로 이동
        return;
    }
    await fetchPosts();
});

// 게시글 목록 가져오기
async function fetchPosts() {
    try {
        const response = await fetch("/posts/api");  // /api/posts로 경로 변경
        if (!response.ok) throw new Error("게시글을 불러올 수 없습니다.");

        const posts = await response.json();
        renderPosts(posts);
    } catch (error) {
        console.error(error);
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
        if (!post.id) return; // ID가 없으면 무시
        const postItem = document.createElement("div");
        postItem.classList.add("post-item");
        postItem.innerHTML = `
            <h3 class="post-title">${post.title}</h3>
            <p class="post-author">${post.author}</p> 
            <p class="post-date">${new Date(post.createdAt).toLocaleString()}</p>
            <p class="post-likes">👍 ${post.likeCount} 좋아요</p>
            <p class="post-comments">💬 ${post.commentCount} 댓글</p>
        `;

        postItem.addEventListener("click", () => {
            if (!post.id) {
                alert("게시글 ID가 없습니다.");
                return;
            }
            window.location.href = `/posts/api/${post.id}`;
        });

        postList.appendChild(postItem);
    });
}