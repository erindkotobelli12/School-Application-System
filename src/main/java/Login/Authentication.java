package Login;


import java.io.*;

public class Authentication {

    public int StudentLoginCheck(String username, String password) throws IOException{
        File file=new File("src/main/database/Login_info_Student.csv");
        if(file.exists() && file.length()>0) {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String line;
            int cnt = 1;
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username) && parts[1].equals(password)) {
                    return cnt;
                }
                cnt++;
            }
            cnt = -1;
            br.close();
        }
        return -1;
    }

    public int ProfessorLoginCheck(String username, String password)throws IOException {
        File file=new File("src/main/database/Login_info_Professor.csv");
        if(file.exists() && file.length()>0){
            FileReader fr= new FileReader(file);
            BufferedReader br= new BufferedReader(fr);

            String line;
            int cnt=1;
            line=br.readLine();
            while((line=br.readLine())!=null){
                String[] parts=line.split(",");
                if(parts[0].equals(username) && parts[1].equals(password)){
                    return cnt;
                }
                cnt++;
            }
            cnt=-1;
            br.close();
        }
        return -1;
    }





}
