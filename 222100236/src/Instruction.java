import java.util.Arrays;

public class Instruction {
    private String type;
    private String eventName; // 第一个参数：比赛名称
    private boolean detail; // 第二个参数：是否包含detail

    public Instruction(String type, String argument) {
        this.type = type;
        this.detail = false; // 默认没有detail参数
        if ("result".equals(type) && argument != null) {
            // 分割参数，检查是否包含detail
            String[] parts = argument.split("\\s+");
            if (parts.length > 1 && "detail".equalsIgnoreCase(parts[parts.length - 1])) {
                this.detail = true; // 如果最后一个参数是detail，则设置detail为true
                // 移除detail参数，剩下的部分为eventName
                this.eventName = capitalizeFirstAndThirdWords(String.join(" ", Arrays.copyOf(parts, parts.length - 1)));
            } else {
                this.eventName = capitalizeFirstAndThirdWords(argument);
            }
        } else {
            this.eventName = argument; // 对于非result类型，eventName直接等于argument
        }
    }

    private String capitalizeFirstAndThirdWords(String argument) {
        String[] words = argument.split(" ");
        if (words.length > 0) {
            words[0] = capitalizeFirstLetter(words[0]);
        }
        if (words.length > 2) {
            words[2] = capitalizeFirstLetter(words[2]);
        }
        return String.join(" ", words);
    }

    private String capitalizeFirstLetter(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public String getType() {
        return type;
    }

    public String getEventName() {
        return eventName;
    }

    public boolean isDetail() {
        return detail;
    }

    @Override
    public String toString() {
        if (this.eventName == null) {
            return this.type;
        } else {
            return this.type + " " + this.eventName + (this.detail ? " detail" : "");
        }
    }
}
