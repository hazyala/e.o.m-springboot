package polytech.aisw.eom.domain;

public enum BoardType {
    SHOW("SHOW", "댄서 포트폴리오와 안무 쉐어"),
    CAST("CAST", "강사, 백업댄서, 팀원 모집"),
    HYPE("HYPE", "배틀, 워크숍, 행사 홍보"),
    LINK("LINK", "연습 파트너와 네트워킹");

    private final String label;
    private final String description;

    BoardType(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }
}

