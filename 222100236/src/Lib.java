import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;

public class Lib {

    // 解析运动员信息的函数
    private static String parseAthlete(Athlete athlete, String countryName) {
        String gender = athlete.getGender() == 0 ? "Male" : "Female";
        String fullName = athlete.getPreferredLastName() + " " + athlete.getPreferredFirstName();
        return String.format("Full Name: %s\nGender: %s\nCountry: %s\n-----\n", fullName, gender, countryName);
    }

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

    // 读取输入文件中的指令
    public static String readInstructionFromFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
    }

    // 将数据写入到输出文件
    public static void writeDataToFile(String inputFilePath, String outputFilePath) throws IOException {
        // 首先，从输入文件中读取指令
        String instruction = new String(Files.readAllBytes(Paths.get(inputFilePath)), StandardCharsets.UTF_8);

        // 检查指令是否为 "players"
        if (instruction.trim().equalsIgnoreCase("players")) {
            // 如果是，获取运动员信息
            String athletesData = getAthletes();

            // 将运动员数据写入到输出文件中
            Files.write(Paths.get(outputFilePath), athletesData.getBytes(StandardCharsets.UTF_8));
        } else {
            // 如果指令不是 "player"，可以选择不做任何操作或者执行其他操作
            System.out.println("指令不是 'players'。没有执行任何操作。");
        }
    }

    // 主函数用于测试
    public static void main(String[] args) throws IOException {
        System.out.print(Lib.getAthletes());
    }
}