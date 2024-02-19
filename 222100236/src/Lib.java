import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Lib {

    // 解析运动员信息
    private static String parseAthlete(Athlete athlete, String countryName) {
        String gender = athlete.getGender() == 0 ? "Male" : "Female";
        String fullName = athlete.getPreferredLastName() + " " + athlete.getPreferredFirstName();
        return String.format("Full Name: %s\nGender: %s\nCountry: %s\n-----\n", fullName, gender, countryName);
    }

    // 解析决赛信息
    private static StringBuilder parseFinalResult(CompetitionResult.Result result) {
        StringBuilder sb = new StringBuilder();

        // 获取需要输出的字段
        String fullName = result.getFullName();
        int rank = result.getRank();
        List<CompetitionResult.Dive> dives = result.getDives();

        sb.append("Full Name: ").append(fullName).append("\n");
        sb.append("Rank: ").append(rank).append("\n");
        sb.append("Score: ");
        double totalPoints = 0;

        for (CompetitionResult.Dive dive : dives) {
            String divePoints = dive.getDivePoints();
            sb.append(divePoints);
            totalPoints += Double.parseDouble(divePoints);
            if (dives.indexOf(dive) < dives.size() - 1) {
                sb.append(" + ");
            }
        }
        sb.append(" = ").append(String.format("%.2f", totalPoints));
        sb.append("\n-----\n");
        return sb;
    }


    // 获取赛事Id
    public static String getEventIdByDisciplineName(String disciplineName) throws IOException {
        String filePath = "src/data/event.json";
        // 读取JSON文件内容
        String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        // 使用Gson将JSON字符串转换为Event对象
        Gson gson = new Gson();
        Event event = gson.fromJson(content, Event.class);

        // 遍历Event对象寻找匹配的DisciplineName并返回其Id
        if (event.getSports() != null) {
            for (Event.Sport sport : event.getSports()) {
                if (sport.getDisciplineList() != null) {
                    for (Event.Discipline discipline : sport.getDisciplineList()) {
                        if (disciplineName.equals(discipline.getDisciplineName())) {
                            return discipline.getId(); // 找到匹配的DisciplineName，返回其Id
                        }
                    }
                }
            }
        }
        return "Discipline not found"; // 如果未找到匹配的DisciplineName，返回提示信息
    }

    // 获取决赛结果
    public static String getFinalResult(String name) throws IOException {
        String id = getEventIdByDisciplineName(name);
        String filePath = "src/data/results/" + id + ".json";
        StringBuilder res = new StringBuilder();

        // 读取JSON文件内容
        String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        // 使用Gson将JSON字符串转换为Event对象
        Gson gson = new Gson();
        CompetitionResult competitionResult = gson.fromJson(content, CompetitionResult.class);

        // 获取决赛运动员信息
        for (CompetitionResult.Heat heat : competitionResult.Heats) {
            if ("Final".equals(heat.Name)) { // 检查是否为Final
                for (CompetitionResult.Result result : heat.Results) {
                    res.append(parseFinalResult(result));
                }
                break; // 找到final后就可以停止搜索
            }
        }

        return res.toString();
    }

    // 获取运动员信息
    public static String getAthletes() throws IOException {
        // 文件路径
        String filePath = "src/data/athletes.json";

        StringBuilder ath = new StringBuilder();
        String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);

        // 解析JSON数据
        Gson gson = new Gson();
        Country[] countries = gson.fromJson(content, Country[].class);

        // 获取所有运动员信息
        for (Country country : countries) {
            for (Athlete athlete : country.getParticipations()) {
                ath.append(parseAthlete(athlete, country.getCountryName()));
            }
        }
        return ath.toString();
    }

    // 获取输入文件中的指令
    public static List<Instruction> getInstructionsFromFile(String filePath) throws IOException {
        List<Instruction> instructions = new ArrayList<>();
        String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.equals("players")) {
                instructions.add(new Instruction("players", null));
            } else if (line.startsWith("result")) {
                String argument = line.substring("result".length()).trim();
                instructions.add(new Instruction("result", argument));
            }
        }
        return instructions;
    }

    // 将数据写入到输出文件
    public static void writeDataToFile(String inputFilePath, String outputFilePath) throws IOException {
        // 从输入文件中读取指令
        List<Instruction> instructions = getInstructionsFromFile(inputFilePath);

        StringBuilder dataToWrite = new StringBuilder();

        for (Instruction instruction : instructions) {
            if ("players".equalsIgnoreCase(instruction.getType())) {
                // 获取运动员信息并追加到dataToWrite
                String athletesData = getAthletes();
                dataToWrite.append(athletesData).append("\n");
            } else if ("result".equalsIgnoreCase(instruction.getType())) {
                // 根据指令的argument获取比赛结果并追加到dataToWrite
                String resultData = getFinalResult(instruction.getArgument());
                dataToWrite.append(resultData).append("\n");
            }
        }

        // 将收集的数据写入到输出文件中
        Files.write(Paths.get(outputFilePath), dataToWrite.toString().getBytes(StandardCharsets.UTF_8));
    }

    // 主函数用于测试
    public static void main(String[] args) throws IOException {
        System.out.print(Lib.getAthletes());
        System.out.print(Lib.getFinalResult("Women 1m Springboard"));
    }
}