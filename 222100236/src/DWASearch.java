import java.io.IOException;

public class DWASearch {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("命令行应输入两个参数: <输入文件> <输出文件>");
            System.exit(-1);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try {
            Lib.writeDataToFile(inputFile, outputFile);
        } catch (IOException e) {
            System.out.println("发生错误：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
