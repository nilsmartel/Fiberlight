
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Parser {

    // void -> Mesh
    public static Model parseObjFile( String fileURL ){
        try{
            File file = new File(fileURL);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            String title = "";
            List<Vector> verts = new ArrayList<Vector>();
            List<Vector> normals = new ArrayList<Vector>();
            List<Vector> coordinates = new ArrayList<Vector>();
            List<Face> faces = new ArrayList<Face>();
            List<String> materials = new ArrayList<>();
            String activeMaterial = "";

            while ((line = bufferedReader.readLine()) != null){
                // [parse] -> line :: String
                String[] str = line.split(" ");
                // Title of Object
                if(str[0].equals("o")){
                    for(int i=1;i<str.length-1;i++){
                        title+=str[i] + " ";
                    }
                    title+=str[str.length-1];
                    continue;
                }
                // Vertex
                if(str[0].equals("v")){
                    verts.add( new Vector(
                            Double.parseDouble(str[1]),
                            Double.parseDouble(str[2]),
                            Double.parseDouble(str[3])
                    ) );
                    continue;
                }
                // Vertex Normals
                if(str[0].equals("vn")){
                    normals.add( new Vector(
                            Double.parseDouble(str[1]),
                            Double.parseDouble(str[2]),
                            Double.parseDouble(str[3])
                    ) );
                    continue;
                }
                // UV Coordinates
                if(str[0].equals("vt")){
                    coordinates.add( new Vector(
                            Double.parseDouble(str[1]),
                            Double.parseDouble(str[2])
                    ) );
                    continue;
                }
                // Polygon / Face
                if(str[0].equals("f")){
                    if(str.length == 4 ){
                        faces.add(
                                new Face( str[1], str[2], str[3], activeMaterial )
                        );
                    }
                    if(str.length == 5 ){
                        faces.add(
                                new Face(str[1], str[2], str[3], activeMaterial )
                        );

                        faces.add(
                                new Face(str[3], str[4], str[1], activeMaterial )
                        );
                    }

                    continue;
                }
                if(str[0].equals("usemtl")){
                    materials.add( str[1] );
                    activeMaterial = str[1];
                }
            }
            fileReader.close();

            return new Model( title, verts, normals, coordinates, faces );
        }catch (IOException e){
            e.printStackTrace();
            return new Model();
        }

    }
}