package polytech.aisw.eom.service;

import org.springframework.data.domain.Sort;

public enum PostSortOption {
    LATEST("latest", "최신순", Sort.by(Sort.Direction.DESC, "createdAt")),
    VIEWS("views", "조회순", Sort.by(Sort.Direction.DESC, "viewCount").and(Sort.by(Sort.Direction.DESC, "createdAt"))),
    COMMENTS("comments", "댓글순", Sort.by(Sort.Direction.DESC, "commentCount").and(Sort.by(Sort.Direction.DESC, "createdAt"))),
    LIKES("likes", "좋아요순", Sort.by(Sort.Direction.DESC, "likeCount").and(Sort.by(Sort.Direction.DESC, "createdAt")));

    private final String key;
    private final String label;
    private final Sort sort;

    PostSortOption(String key, String label, Sort sort) {
        this.key = key;
        this.label = label;
        this.sort = sort;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public Sort getSort() {
        return sort;
    }

    public static PostSortOption from(String key) {
        for (PostSortOption option : values()) {
            if (option.key.equalsIgnoreCase(key)) {
                return option;
            }
        }
        return LATEST;
    }
}
