package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaNotSupportedException;
import ar.edu.utn.frbb.tup.model.exception.TipoMonedaNotSupportedException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CuentaServiceTest {

    @Mock
    private CuentaDao cuentaDao;
    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private CuentaService cuentaService;
    

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    //Generar casos de test para darDeAltaCuenta
    //    1 - cuenta existente
    //    2 - cuenta no soportada
    //    3 - cliente ya tiene cuenta de ese tipo
    //    4 - cuenta creada exitosamente
    @Test
    public void cuentaExistenteFail() throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException, TipoCuentaNotSupportedException, TipoMonedaNotSupportedException {
        Cuenta cuenta = new Cuenta()
                .setNumeroCuenta(123456789) // edite el set de Cuenta.java porque estaba mal
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        when(cuentaDao.find(cuenta.getNumeroCuenta())).thenReturn(cuenta);

        // Espero que me tire la excepción y que no me guarde la cuenta
        assertThrows(CuentaAlreadyExistsException.class, () -> {
            cuentaService.darDeAltaCuenta(cuenta, 373898088);});
        
        verify(cuentaDao, times(0)).save(cuenta);
    }

    @Test
    public void cuentaNoSoportadaFail() throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException, TipoCuentaNotSupportedException, TipoMonedaNotSupportedException {
        Cuenta cuenta = new Cuenta()
                .setNumeroCuenta(123456789)
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);

        Cuenta cuentaMismoTipo = new Cuenta()
                .setNumeroCuenta(35353535)
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);

        Cuenta cuentaCajaAhorro = new Cuenta()
                .setNumeroCuenta(1323213231)
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        //Configuro dos arraylist, uno para que devuelve una caja de ahorro, el otro para que devuelva tambien la cuenta corriente
        List<Cuenta> cuentasInicial = new ArrayList<>();
        cuentasInicial.add(cuentaCajaAhorro);

        List<Cuenta> cuentasCliente = new ArrayList<>();
        cuentasCliente.add(cuentaCajaAhorro);
        cuentasCliente.add(cuenta);

        // Configurar mocks
        when(cuentaDao.find(anyLong())).thenReturn(null); 
        // Configuro para que en el primer llamado a getCuentas me devuelva la CA y en el segundo CC y CA
        when(cuentaDao.getCuentasByCliente(373898088))
                .thenReturn(cuentasInicial) // Primer llamada
                .thenReturn(cuentasCliente); // Segunda llamada

        // En el primero doy de alta una cuenta con CC
        cuentaService.darDeAltaCuenta(cuenta, 373898088);

        // En el segundo verifico que me tire la excepcion porque el cliente ya tiene CC
        assertThrows(TipoCuentaAlreadyExistsException.class, () -> {
            cuentaService.darDeAltaCuenta(cuentaMismoTipo, 373898088);
        });

        // Por ultimo verifico los guardados de cuenta
        verify(cuentaDao, times(1)).save(cuenta);
        verify(cuentaDao, times(0)).save(cuentaMismoTipo);
    }

    @Test
    public void cuentaCreadaSuccess() throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException, TipoCuentaNotSupportedException, TipoMonedaNotSupportedException {
        Cuenta cuenta = new Cuenta()
                .setNumeroCuenta(123456789)
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
        
        when(cuentaDao.find(cuenta.getNumeroCuenta())).thenReturn(null);
        
        // Configuro el mock para que cuando haga el getCuentas me devuelva una lista vacia
        when(cuentaDao.getCuentasByCliente(373898088))
                .thenReturn(new ArrayList<>()); // Primer llamada

        // Creo la primera cuenta
        cuentaService.darDeAltaCuenta(cuenta, 373898088);

        verify(cuentaDao, times(1)).save(cuenta);
    }



}
