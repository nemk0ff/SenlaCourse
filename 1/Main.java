public class Main {
    public static void main(String[] args) {
        java.util.Random random = new java.util.Random();
        int number = random.nextInt(900) + 100;

        System.out.println("Число: " + number);

        System.out.println("Максимальная цифра: " + sumDigits(number));
    }

    public static int sumDigits(int num){
        int ans = 0;
        while(num > 0) {
            if((num % 10) > ans)
                ans = num % 10;
            num /= 10;
        }
        return ans;
    }
}