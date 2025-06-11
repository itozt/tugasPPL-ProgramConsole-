public class PerformanceCriteria {
    private int criterionId;
    private String name;
    private String description;
    private int maxScore;
    private boolean isActive;

    public PerformanceCriteria(int criterionId, String name, String description, int maxScore, boolean isActive) {
        this.criterionId = criterionId;
        this.name = name;
        this.description = description;
        this.maxScore = maxScore;
        this.isActive = isActive;
    }

    // Getters
    public int getCriterionId() { return criterionId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getMaxScore() { return maxScore; }
    public boolean isActive() { return isActive; }
}