public class Instruction {
    private String type;
    private String argument;

    public Instruction(String type, String argument) {
        this.type = type;
        if ("result".equals(type) && argument != null) {
            this.argument = capitalizeFirstAndThirdWords(argument);
        } else {
            this.argument = argument;
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

    public String getArgument() {
        return argument;
    }

    @Override
    public String toString() {
        if (this.argument == null) {
            return this.type;
        } else {
            return this.type + " " + this.argument;
        }
    }
}
