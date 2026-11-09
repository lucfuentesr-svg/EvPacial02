package cl.duoc.viviendas.repository;

import cl.duoc.viviendas.model.Vivienda;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GsonViviendaDao<T extends Vivienda> implements ViviendaDao<T> {
    private final Path archivo;
    private final Type tipoLista;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public GsonViviendaDao(String archivo,Class<T> clase) {
        //TODO: Implementar carpeta para datos
        this.archivo = Path.of(archivo);
        this.tipoLista = TypeToken.getParameterized(List.class, clase).getType();
    }

    @Override
    public List<T> leer() {
        if(Files.notExists(this.archivo)){
            return new ArrayList<>();
        }
        try(Reader lector = Files.newBufferedReader(this.archivo, StandardCharsets.UTF_8)){
            List<T> viviendas = this.gson.fromJson(lector, this.tipoLista);
            if(viviendas == null){
                return new ArrayList<>();
            }
            List<T> validos = new ArrayList<>();
            for(T vivienda : viviendas){
                if(vivienda != null && vivienda.getCodigo()!=null){
                    validos.add(vivienda);
                }
            }
            return validos;
        }catch (IOException e){
            throw new PersistenciaException(PersistenciaException.ALERTA_LECTURA,e);
        }
    }

    @Override
    public void guardar(List<T> viviendas) {
        try(Writer escritor = Files.newBufferedWriter(this.archivo,StandardCharsets.UTF_8)){
            this.gson.toJson(viviendas,this.tipoLista, escritor);
        }catch (IOException | JsonParseException e){
            throw new PersistenciaException(PersistenciaException.ALERTA_ESCRITURA,e);
        }
    }
}
