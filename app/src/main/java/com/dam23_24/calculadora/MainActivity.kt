package com.dam23_24.calculadora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.text.DecimalFormat

@Suppress("SpellCheckingInspection")
class MainActivity : AppCompatActivity() {

    private lateinit var txtPantalla: TextView
    private lateinit var txtDetalle: TextView

    private lateinit var btnNum: ArrayList<Button>
    private lateinit var btnOper: ArrayList<Button>

    private lateinit var btnCE: Button
    private lateinit var btnResult: Button

    private lateinit var calc: Calculo
    private val df = DecimalFormat("#.##")

    //Variable lateinit boton que se relacionara con el boton CE de la calculadora
    private lateinit var botonquitarcaracter: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Creo mi objeto de tipo Calculo.
        calc = Calculo()

        //Inicializamos las variables que corresponderán a cada componente y les asignamos una función al evento que se programa de cada uno.
        initComponents()
        initListeners()
    }

    /**
     * Inicializar las variables que se asignarán a cada componente que vamos a controlar.
     */
    private fun initComponents() {
        txtPantalla = findViewById(R.id.txtPantalla)
        txtDetalle = findViewById(R.id.txtDetalle)

        btnCE = findViewById(R.id.btnCE)
        btnResult = findViewById(R.id.btnResult)

        txtPantalla.text = ""
        txtDetalle.text = ""

        initBtnNum()
        initBtnOper()
    }

    /**
     * Establecer las variables del ArraList btnNum que controlarán los dígitos y el punto decimal.
     */
    private fun initBtnNum() {
        btnNum = ArrayList()
        btnNum.add(findViewById(R.id.btn0))
        btnNum.add(findViewById(R.id.btn1))
        btnNum.add(findViewById(R.id.btn2))
        btnNum.add(findViewById(R.id.btn3))
        btnNum.add(findViewById(R.id.btn4))
        btnNum.add(findViewById(R.id.btn5))
        btnNum.add(findViewById(R.id.btn6))
        btnNum.add(findViewById(R.id.btn7))
        btnNum.add(findViewById(R.id.btn8))
        btnNum.add(findViewById(R.id.btn9))
        btnNum.add(findViewById(R.id.btnDec))
    }

    /**
     * Establecer las variables del ArraList btnOper que controlarán los operadores del cálculo.
     */
    private fun initBtnOper() {
        btnOper = ArrayList()
        btnOper.add(findViewById(R.id.btnSuma))
        btnOper.add(findViewById(R.id.btnResta))
        btnOper.add(findViewById(R.id.btnMul))
        btnOper.add(findViewById(R.id.btnDiv))
    }

    /**
     * Establecer los eventos y las funciones asociadas de cada componente que vamos a controlar.
     */
    private fun initListeners() {
        for (i in 0..<btnNum.count()) {
            btnNum[i].setOnClickListener { btnNumClicked(i) }
        }

        for (i in 0..<btnOper.count()) {
            btnOper[i].setOnClickListener { btnOperClicked(i) }
        }
        //Se enlaza la variable con el boton, mediante su id
        botonquitarcaracter = findViewById(R.id.botonquitar)
        btnCE.setOnClickListener { btnCEClicked() }
        btnResult.setOnClickListener { btnResultClicked() }
        //Cada vez que se pulse el boton se llama a la funcion que contiene el codigo para borrar digitos
        botonquitarcaracter.setOnClickListener { eliminarcaracter() }
    }

    /**
     * Agrega el dígito pulsado en el número correspondiente del objeto calc.
     *
     * @param num dígito pulsado del 0 al 9 o punto decimal (10)
     */
    private fun btnNumClicked(num: Int) {
        calc.tecleaDigito(num)

        //Mostramos info actualizada en los TextView de la app
        if (calc.primerNum) {
            muestraValor(calc.numTemp1, calc.numTemp1)
        } else {
            muestraValor(calc.numTemp2, calc.numTemp1 + calc.operadorTxt() + calc.numTemp2)
        }
    }

    /**
     * Agrega la operación del cálculo a realizar
     *
     * @param num operación (0 -> + / 1 -> - / 2 -> * / 3 -> /
     */
    private fun btnOperClicked(num: Int) {
        if (calc.primerNum) {
            //Tratamiento de la operación cuando estamos introduciendo el primer número.

            if (calc.numCalculos > 0 && calc.numTemp1 == "") {
                //Si hay un cálculo anterior y el num1 aún está vacío, el resultado anterior es el num1 del siguiente cálculo.
                calc.num1 = calc.result
                calc.numTemp1 = df.format(calc.result).toString()
            } else {
                //Sino, asignamos num1 del objeto calc convirtiendo los dígitos introducidos a float.
                //Además, si existe algún problema o cuando si se pulsa un operador sin introducir número antes, lo capturamos y usamos el valor 0.
                try {
                    calc.num1 = calc.numTemp1.toFloat()
                } catch (e: NumberFormatException) {
                    calc.num1 = 0f
                    calc.numTemp1 = "0"
                }
            }

            //Asignamos el operador al objeto calc, mostramos info en pantalla y actualizamos las características necesarias de calc para indicar que pasamos al estado de introducir el segundo número.
            calc.op = num
            muestraValor(calc.operadorTxt(), calc.numTemp1 + calc.operadorTxt())
            calc.numTemp2 = ""
            calc.primerNum = false
        } else if (calc.numTemp2 == "") {
            //Si se introduce una operación y aún no existe el segundo número la nueva operación debe reemplazar la operación anterior.

            calc.op = num
            //Mostramos en pantalla la actualización del operador.
            muestraValor(calc.operadorTxt(), calc.numTemp1 + calc.operadorTxt())
        } else {
            //Tratamiento de la operación cuando estamos introduciendo el segundo número.

            //Convertimos la cadena de dígitos en el número 2 y realizamos el cálculo.
            //Si existe algún problema en la conversión la controlamos asignando el valor 0.
            calc.num2 = try {
                calc.numTemp2.toFloat()
            } catch (e: NumberFormatException) {
                0f
            }
            calc.calcular()

            //Mostramos en pantalla el resultado del cálculo como detalle y la operación en la pantalla principal.
            muestraValor(
                calc.operadorTxt(num),
                df.format(calc.result).toString() + calc.operadorTxt(num)
            )

            //Actualizamos las características necesarias del objeto calc, ya que vamos a seguir en el estado de introducir solo un segundo número, ya que el primer número y la operación es asignado como el resultado del cálculo realizado y la nueva operación introducida.
            calc.num1 = calc.result
            calc.op = num
            calc.num2 = 0f
            calc.numTemp1 = df.format(calc.num1).toString()
            calc.numTemp2 = ""
        }
    }

    /**
     * Reiniciar las características del objeto calc cuando se pulsa el botón CE
     */
    private fun btnCEClicked() {
        //Mostramos en pantalla y detalle la cadena de caracteres vacía.
        muestraValor("", "")

        //Inicializamos las características del objeto calc.
        calc.iniValores()
    }

    /**
     * Acciones a realizar al pulsar el botón =.
     * Solo se ejecutará si estamos introduciendo el segundo número.
     * Realizará las acciones necesarias para mostrar el cálculo en pantalla.
     */
    private fun btnResultClicked() {
        if (!calc.primerNum && calc.numTemp2 != "") {
            //Si estamos introduciendo el segundo número, lo actualizamos convirtiendo la cadena de dígitos y calculamos la operación.
            calc.num2 = try {
                calc.numTemp2.toFloat()
            } catch (e: NumberFormatException) {
                0f
            }
            calc.calcular()

            //Mostramos en pantalla el resultado y en detalle toda la operación (num1 + num2 = result) formateando a 2 posiciones decimales.
            muestraValor(
                df.format(calc.result).toString(),
                df.format(calc.num1).toString() + calc.operadorTxt() + df.format(calc.num2)
                    .toString() + "=" + df.format(calc.result).toString()
            )

            //Inicializamos las características del objeto calc, excepto el núemro de cálculos.
            calc.iniValores(resetNumCalculos = false, resetResult = false)
        } else {
            mensajeError("Debe introducir 2 números y una operación para mostrar un resultado")
        }
    }

    /**
     * Muestra la información en los componentest TextView txtPantalla y txtDetalle.
     *
     * @param pantalla info a mostrar en txtPantalla
     * @param detalle info a mostrar en txtDetalle
     */
    private fun muestraValor(pantalla: CharSequence, detalle: CharSequence) {
        txtPantalla.text = getString(R.string.txt_txtPantalla, pantalla)
        txtDetalle.text = getString(R.string.txt_txtDetalle, detalle)
    }


    /**
     * Muestra un mensaje de error en pantalla durante un tiempo corto.
     *
     * @msj mensaje de error
     */
    private fun mensajeError(msj: String) {
        Toast.makeText(this, msj, Toast.LENGTH_SHORT).show()
    }


    //FUNCION ELIMINAR CARACTER
    private fun eliminarcaracter() {
        /**
         * Se comprueba en que parte del codigo nos encontramos
         */
        if (calc.primerNum)
        {
            /**
             * Se mira si no se ha introducido ningun numero, si es asi, saldra un aviso
             */
            if (calc.numTemp1 == "")
            {
                Toast.makeText(this, "NO HAY NADA QUE BORRAR", Toast.LENGTH_SHORT).show()
            }
            else
            /**
             * Si hay contenido en el numero1 temporal, se borrará un digito del numero,
             * a su vez se eliminara un digito tanto de la pantalla como de la pantalla de detalle
             */
            calc.numTemp1 = calc.numTemp1.dropLast(1)
            txtPantalla.text = txtPantalla.text.dropLast(1)
            txtDetalle.text = txtDetalle.text.dropLast(1)
        }
        /**
         * Parte del codigo una vez calcu.primernum es falso, es decir, ya se ha introducido num1
         */
        else if (calc.primerNum == false) {

            /**
             * Si el primernum es falso, es decir, estamos introduciendo el numero2 temporal, pero este esta vacio (es decir, solo se ha introducido la operacion)
             * se borrara el digito de la operacion, ademas que asigaremos la operacion a 5, un rango que no esta entre entre 0-3, que corresponden a las operaciones.
             * Una vez cambiado la operacion, borramos digito de la pantalla y detalle, ademas de que 'calcu.primernum' pasa a ser verdadero, ya que
             * como hemos eliminado la operacion, aun estariamos introduciendo num1 temporal hasta que se introduzca de nuevo un operador
             */
            if (calc.numTemp2 == "") {
                if (calc.op >= 0 && calc.op <4) {
                    calc.op = 5
                    txtDetalle.text=txtDetalle.text.dropLast(1)
                    txtPantalla.text=txtPantalla.text.dropLast(1)
                    calc.primerNum=true
                }
            }
            /**
             * En caso de que si se haya introducido valor en num2 temporal, podra ser eliminado
             */
            else if (calc.numTemp2!=""){
                txtPantalla.text=txtPantalla.text.dropLast(1)
                txtDetalle.text=txtDetalle.text.dropLast(1)
                calc.numTemp2=calc.numTemp2.dropLast(1)
            }
        }
    }
}