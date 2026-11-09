package cl.duoc.viviendas.repository;

import cl.duoc.viviendas.model.Vivienda;
import java.util.List;

public interface ViviendaDao<T extends Vivienda> {
    List<T> leer();
    void guardar(List<T> viviendas);
}
