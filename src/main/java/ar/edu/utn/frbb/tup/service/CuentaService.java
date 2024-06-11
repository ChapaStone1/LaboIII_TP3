package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaNotSupportedException;
import ar.edu.utn.frbb.tup.model.exception.TipoMonedaNotSupportedException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CuentaService {
    CuentaDao cuentaDao = new CuentaDao();

    @Autowired
    ClienteService clienteService;

    //Generar casos de test para darDeAltaCuenta
    //    1 - cuenta existente
    //    2 - cuenta no soportada
    //    3 - cliente ya tiene cuenta de ese tipo
    //    4 - cuenta creada exitosamente
    public void darDeAltaCuenta(Cuenta cuenta, long dniTitular) throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException, TipoCuentaNotSupportedException, TipoMonedaNotSupportedException {
        if(cuentaDao.find(cuenta.getNumeroCuenta()) != null) {
            throw new CuentaAlreadyExistsException("La cuenta " + cuenta.getNumeroCuenta() + " ya existe.");
        }

        if (!tipoCuentaYMonedaSoportada(cuenta)) {
            throw new TipoCuentaNotSupportedException("El tipo de cuenta " + cuenta.getTipoCuenta() + " y/o el tipo de moneda " + cuenta.getMoneda() + " no está soportado.");
        }

        if (cuentaMismoTipo(cuenta, cuentasdeCliente(dniTitular))){
            throw new TipoCuentaAlreadyExistsException("El cliente " + dniTitular + " ya tiene una cuenta de tipo " + cuenta.getTipoCuenta() + ".");
        }

        clienteService.agregarCuenta(cuenta, dniTitular);
        cuentaDao.save(cuenta);
    }

    public Cuenta find(long id) {
        return cuentaDao.find(id);
    }
    // Valido que cuenta y moneda sean soportadas
    private boolean tipoCuentaYMonedaSoportada(Cuenta cuenta) {
        return (cuenta.getTipoCuenta() == TipoCuenta.CAJA_AHORRO && (cuenta.getMoneda() == TipoMoneda.PESOS || cuenta.getMoneda() == TipoMoneda.DOLARES)) ||
               (cuenta.getTipoCuenta() == TipoCuenta.CUENTA_CORRIENTE && cuenta.getMoneda() == TipoMoneda.PESOS);
    }

    // Valido que el cliente no tenga cuentas del mismo tipo
    private boolean cuentaMismoTipo(Cuenta cuenta, List<Cuenta> cuentasCliente) {
        for (Cuenta cuentaDelCliente : cuentasCliente) {
            if (cuentaDelCliente.getTipoCuenta() == cuenta.getTipoCuenta()) {
                return true;
            }
        }
        return false;
    }
    // Creo metodo para que me devuelva la lista de un cliente, en caso de que no tenga me devuelve lista vacia
    private List<Cuenta> cuentasdeCliente(long dni){
        List<Cuenta> cuentas = cuentaDao.getCuentasByCliente(dni);
        return cuentas != null ? cuentas : new ArrayList<>();
    }

}
