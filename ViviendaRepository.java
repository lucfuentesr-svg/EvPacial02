package cl.duoc.viviendas.repository;

import cl.duoc.viviendas.model.Vivienda;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Casa;

import java.util.ArrayList;
import java.util.List;

public class ViviendaRepository {
    public static final String ARCHIVO_DEPARTAMENTOS = "departamentos.json";
    public static final String ARCHIVO_CASAS = "casas.json";

    private final ViviendaDao<Departamento> departamentosDao;
    private final ViviendaDao<Casa> casasDao;
    private final List<Departamento> departamentos = new ArrayList<>();
    private final List<Casa> casas = new ArrayList<>();

    public ViviendaRepository(){
        this(new GsonViviendaDao<>(ARCHIVO_DEPARTAMENTOS, Departamento.class),new GsonViviendaDao<>(ARCHIVO_CASAS, Casa.class));
    }

    public ViviendaRepository(ViviendaDao<Departamento> departamentosDao, ViviendaDao<Casa> casasDao){
        this.departamentosDao = departamentosDao;
        this.casasDao = casasDao;
    }

    public void cargar(){
        List<Departamento> leidosDepartamentos = this.departamentosDao.leer();
        List<Casa> leidosCasas = this.casasDao.leer();
        this.departamentos.clear();
        this.departamentos.addAll(leidosDepartamentos);
        this.casas.clear();
        this.casas.addAll(leidosCasas);
    }

    public List<Vivienda> listar(){
        List<Vivienda> todos = new ArrayList<>(this.departamentos);
        todos.addAll(this.casas);

        return todos;
    }

    public void registrar(Vivienda vivienda){
        if(vivienda instanceof Departamento departamento){
            this.departamentos.add(departamento);
            try{
                departamentosDao.guardar(this.departamentos);
            }catch(PersistenciaException e){
                this.departamentos.remove(departamentos.size()-1);
                throw e;
            }
        } else if (vivienda instanceof Casa casa) {
            this.casas.add(casa);
            try{
                casasDao.guardar(this.casas);
            }catch(PersistenciaException e){
                this.casas.remove(casas.size()-1);
                throw e;
            }

        }else{
            throw new IllegalArgumentException("Tipo de vivienda no soportado");
        }
    }

    public boolean eliminar(Vivienda vivienda){
        if(vivienda instanceof Departamento){
            return this.eliminarDe(departamentos,departamentosDao,vivienda);
        } else if (vivienda instanceof Casa) {
            return this.eliminarDe(casas,casasDao,vivienda);
        }
        return false;
    }

    private <T extends Vivienda> boolean eliminarDe(List<T> lista,ViviendaDao<T> dao,Vivienda vivienda){
        int posicion = indiceDe(lista, vivienda);
        if(posicion < 0){
            return false;
        }
        T eliminado = lista.remove(posicion);
        try{
            dao.guardar(lista);
        }catch (PersistenciaException e){
            lista.add(posicion, eliminado);
            throw e;
        }
        return true;
    }

    private static int indiceDe(List<? extends Vivienda> lista, Vivienda vivienda){
        for(int i = 0; i < lista.size(); i++){
            if(lista.get(i) == vivienda){
                return i;
            }
        }
        return -1;
    }

}
