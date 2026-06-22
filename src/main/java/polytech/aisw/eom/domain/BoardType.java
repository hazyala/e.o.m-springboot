package polytech.aisw.eom.domain;

public enum BoardType {
    SHOW("SHOW", "포트폴리오, 공연 기록, 연습 영상, 안무 쉐어 등 나의 활동을 기록하고 보여주는 공간입니다."),
    CAST("CAST", "강사, 백업댄서, 팀원, 오디션 모집처럼 함께 움직일 사람과 기회를 찾는 공간입니다."),
    HYPE("HYPE", "배틀, 워크숍, 쇼케이스, 파티와 같은 현장의 열기와 일정을 공유하는 공간입니다."),
    LINK("LINK", "연습 파트너, 촬영 메이트, 공간 공유, 피드백 모임처럼 댄서들이 서로 연결되는 공간입니다.");

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
