package github.com.matcwa.api.dto;

public class NewPollDto {
    private String name;
    private boolean manyVotePerQuestion;

    public NewPollDto() {
    }

    public NewPollDto(String name) {
        this.name = name;
    }

    public NewPollDto(String name, boolean manyVotePerQuestion) {
        this.name = name;
        this.manyVotePerQuestion=manyVotePerQuestion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isManyVotePerQuestion() {
        return manyVotePerQuestion;
    }

    public void setManyVotePerQuestion(boolean manyVotePerQuestion) {
        this.manyVotePerQuestion = manyVotePerQuestion;
    }
}
