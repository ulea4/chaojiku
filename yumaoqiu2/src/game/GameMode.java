package game;

public enum GameMode {
    VERSUS("双人对战", "两名玩家对战"),
    TIME_ATTACK("时间挑战", "限时得分挑战"),
    PRACTICE("练习模式", "单人练习"),
    TOURNAMENT("锦标赛", "多轮比赛");

    private String name;
    private String description;

    GameMode(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
}