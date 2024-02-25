import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Lib {
    private static final Map<String, Map<String, Map<String, String>>> eventAthleteScores = new HashMap<>();     // 存储每个赛事的运动员信息

    // 解析运动员信息
    private static String parseAthlete(Athlete athlete, String countryName) {
        String gender = athlete.getGender() == 0 ? "Male" : "Female";
        String fullName = athlete.getPreferredLastName() + " " + athlete.getPreferredFirstName();
        return String.format("Full Name:%s\nGender:%s\nCountry:%s\n-----\n", fullName, gender, countryName);
    }

    // 解析决赛信息
    private static StringBuilder parseFinalResult(CompetitionResult.Result result) {
        StringBuilder sb = new StringBuilder();

        // 获取需要输出的字段
        String fullName = result.getFullName();
        int rank = result.getRank();
        List<CompetitionResult.Dive> dives = result.getDives();

        sb.append("Full Name:").append(fullName).append("\n");
        sb.append("Rank:").append(rank).append("\n");
        sb.append("Score:");
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

    // 解析赛事不同阶段的详细信息
    private static void parseAllResults(List<CompetitionResult.Result> results, String stage, String event) {
        Map<String, Map<String, String>> athleteScores = eventAthleteScores.computeIfAbsent(event, k -> new HashMap<>());

        for (CompetitionResult.Result result : results) {
            String fullName = result.getFullName().contains("/") ? formatSynchronisedFullName(result) : result.getFullName();
            Map<String, String> scores = athleteScores.computeIfAbsent(fullName, k -> new HashMap<>());
            scores.put(stage + "Rank", String.valueOf(result.getRank()));
            scores.put(stage + "Score", generateScoreString(result.getDives()));
        }
    }

    // 生成所需的分数
    private static String generateScoreString(List<CompetitionResult.Dive> dives) {
        if (dives == null || dives.isEmpty()) {
            return "*";
        }
        StringBuilder scoreBuilder = new StringBuilder();
        double totalPoints = 0;
        for (CompetitionResult.Dive dive : dives) {
            String divePoints = dive.getDivePoints();
            scoreBuilder.append(divePoints);
            totalPoints += Double.parseDouble(divePoints);
            if (dives.indexOf(dive) < dives.size() - 1) {
                scoreBuilder.append(" + ");
            }
        }
        if (scoreBuilder.length() > 0) {
            scoreBuilder.append(" = ").append(String.format("%.2f", totalPoints));
        }
        return scoreBuilder.toString();
    }

    // 格式化双人赛事中的运动员姓名
    private static String formatSynchronisedFullName(CompetitionResult.Result result) {
        // 将姓名按 "/" 分割，得到两个运动员的姓名
        String[] names = result.getFullName().split("\\s*/\\s*");
        // 对每个姓名分割为姓和名，然后排序
        Arrays.sort(names, Comparator.comparing(name -> name.split(" ")[1])); // 假设姓氏在前
        // 重新组合姓名，格式为 'A & B'
        return names[0] + " & " + names[1];
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

    // 获取某个赛事的决赛结果
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

    // 获取某个赛事的详细结果
    public static String getAllResults(String name) throws IOException {
        String id = getEventIdByDisciplineName(name);
        String filePath = "src/data/results/" + id + ".json";
        StringBuilder res = new StringBuilder();

        // 读取JSON文件内容
        String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        Gson gson = new Gson();
        CompetitionResult competitionResult = gson.fromJson(content, CompetitionResult.class);

        // 获取当前赛事的运动员信息
        for (CompetitionResult.Heat heat : competitionResult.Heats) {
            parseAllResults(heat.Results, heat.Name, name);
        }

        // 所有阶段处理完毕后，进行最终输出
        res.append(finalizeResultsOutput(name));

        return res.toString();
    }

    // 所有阶段处理完毕后，进行最终输出
    public static String finalizeResultsOutput(String event) {
        StringBuilder sb = new StringBuilder();
        Map<String, Map<String, String>> athleteScores = eventAthleteScores.getOrDefault(event, new HashMap<>());

        // 排序逻辑不变，确保按初赛排名排序
        athleteScores.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> {
                    String fullName = e.getKey();
                    Map<String, String> info = e.getValue();
                    String rankStr;
                    // 判断选手全名中是否包含&
                    if (fullName.contains("&")) {
                        // 如果包含&，则使用FinalRank作为排序依据
                        rankStr = info.getOrDefault("FinalRank", "*");
                    } else {
                        // 如果不包含&，则使用PreliminaryRank作为排序依据
                        rankStr = info.getOrDefault("PreliminaryRank", "*");
                    }
                    return rankStr.equals("*") ? Integer.MAX_VALUE : Integer.parseInt(rankStr);
                }))
                .forEach(entry -> {
                    String fullName = entry.getKey();
                    Map<String, String> info = entry.getValue();

                    // 生成排名字符串，正确处理缺失的阶段
                    String rank = Stream.of("PreliminaryRank", "SemifinalRank", "FinalRank")
                            .map(key -> info.getOrDefault(key, "*"))
                            .collect(Collectors.joining(" | "));

                    // 生成得分字符串，正确处理缺失的得分
                    String preliminaryScore = info.getOrDefault("PreliminaryScore", "*");
                    String semifinalScore = info.getOrDefault("SemifinalScore", "*");
                    String finalScore = info.getOrDefault("FinalScore", "*");

                    // 构建最终输出
                    sb.append("Full Name:").append(fullName).append("\n");
                    sb.append("Rank:").append(rank).append("\n");
                    sb.append("Preliminary Score:").append(preliminaryScore).append("\n");
                    sb.append("Semifinal Score:").append(semifinalScore).append("\n");
                    sb.append("Final Score:").append(finalScore).append("\n-----\n");
                });

        // 清理该赛事数据
        eventAthleteScores.remove(event);

        return sb.toString();
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
            // 使用stream()对参与者按照PreferredFirstName进行排序
            List<Athlete> sortedAthletes = country.getParticipations().stream()
                    .sorted(Comparator.comparing(Athlete::getPreferredFirstName))
                    .collect(Collectors.toList());

            for (Athlete athlete : sortedAthletes) {
                ath.append(parseAthlete(athlete, country.getCountryName()));
            }
        }
        return ath.toString();
    }

    // 获取输入文件中的指令
    public static List<Instruction> getInstructionsFromFile(String filePath) throws IOException {
        List<Instruction> instructions = new ArrayList<>();
        List<String> validEvents = Arrays.asList(
                "women 1m springboard", "women 3m springboard", "women 10m platform",
                "women 3m synchronised", "women 10m synchronised",
                "men 1m springboard", "men 3m springboard", "men 10m platform",
                "men 3m synchronised", "men 10m synchronised"
        );

        String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("result ")) {
                String[] parts = line.substring("result ".length()).trim().split(" ", 4); // 最多分割成4部分

                if (parts.length >= 3 && validEvents.contains(String.join(" ", Arrays.copyOf(parts, parts.length - (parts.length == 4 ? 1 : 0))))) {
                    String eventName = String.join(" ", Arrays.copyOf(parts, parts.length - (parts.length == 4 ? 1 : 0)));
                    boolean isDetail = parts.length == 4 && "detail".equals(parts[3]);
                    if (isDetail) {
                        instructions.add(new Instruction("result", eventName + " detail"));
                    } else if (parts.length == 3) {
                        instructions.add(new Instruction("result", eventName));
                    } else {
                        instructions.add(new Instruction("n/a", null)); // 不符合预期的指令格式
                    }
                } else {
                    instructions.add(new Instruction("n/a", null)); // 比赛项目名称无效
                }
            } else if ("players".equals(line)) {
                instructions.add(new Instruction("players", null)); // players指令
            } else {
                instructions.add(new Instruction("error", null)); // 无法识别的指令
            }
        }
        return instructions;
    }

    // 将数据写入到输出文件
    public static void writeDataToFile(String inputFilePath, String outputFilePath) throws IOException {
        List<Instruction> instructions = getInstructionsFromFile(inputFilePath);
        StringBuilder dataToWrite = new StringBuilder();

        for (Instruction instruction : instructions) {
            switch (instruction.getType()) {
                case "players":
                    // getAthletes方法返回所有运动员的信息
                    String athletesData = getAthletes();
                    dataToWrite.append(athletesData);
                    break;
                case "result":
                    // 使用getEventName和isDetail来获取详细信息
                    String eventName = instruction.getEventName();
                    boolean detail = instruction.isDetail();

                    if (detail) {
                        // getAllResults方法返回比赛的详细结果
                        String detailedResultData = getAllResults(eventName);
                        dataToWrite.append(detailedResultData);
                    } else {
                        // getFinalResult方法返回比赛的最终结果
                        String resultData = getFinalResult(eventName);
                        dataToWrite.append(resultData);
                    }
                    break;
                case "n/a":
                    // 指令错误，写入N/A信息
                    dataToWrite.append("N/A\n-----\n");
                    break;
                default:
                    // 对于未识别的指令类型或错误，写入Error信息
                    dataToWrite.append("Error\n-----\n");
                    break;
            }
        }

        // 将收集的数据写入到输出文件中
        Files.write(Paths.get(outputFilePath), dataToWrite.toString().getBytes(StandardCharsets.UTF_8));
    }

    // 主函数用于测试
    public static void main(String[] args) throws IOException {
        //System.out.print(Lib.getAthletes());
        //System.out.print(Lib.getFinalResult("Women 1m Springboard"));
        System.out.print(Lib.getAllResults("Women 10m Platform"));
    }
}