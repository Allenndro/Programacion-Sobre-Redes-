package ar.edu.et32.FlujoDatos;

import java.io.IOException;


public class EjerciciosGuia1 {


	public EjerciciosGuia1() {
		boolean continuar = true;
		while( continuar ) {
			mostrarOpciones();
			int option = pedirNumero();
			
			switch (option) {
			case 0: {
				Utils.getOut().println( Utils.ANSI_PURPLE.concat("\tSALIENDO").concat(Utils.ANSI_RESET) );
				continuar = false;
				break;
			}
			case 1: {
				Utils.getOut().println( Utils.ANSI_BLUE.concat("-------Guia 1 - Ejericio 1--------").concat(Utils.ANSI_RESET) );
				Guia1_Ej1();
				break;
			}
			case 2: {
				Utils.getOut().println( Utils.ANSI_BLUE.concat("-------Guia 1 - Ejericio 2--------").concat(Utils.ANSI_RESET) );
				Guia1_Ej2();
				break;
			}
			case 3: {
				Utils.getOut().println( Utils.ANSI_BLUE.concat("-------Guia 1 - Ejericio 3--------").concat(Utils.ANSI_RESET) );
				Guia1_Ej3();
				break;
			}
			case 4: {
				Utils.getOut().println( Utils.ANSI_BLUE.concat("-------Guia 1 - Ejericio 4--------").concat(Utils.ANSI_RESET) );
				Guia1_Ej4();
				break;
			}
			case 5: {
				Utils.getOut().println( Utils.ANSI_BLUE.concat("-------Guia 1 - Ejericio 5--------").concat(Utils.ANSI_RESET) );
				Guia1_Ej5();
				break;
			}
			case 6: {
				Utils.getOut().println( Utils.ANSI_BLUE.concat("-------Guia 1 - Ejericio 6--------").concat(Utils.ANSI_RESET) );
				Guia1_Ej6();
				break;
			}
			case 7: {
				Utils.getOut().println( Utils.ANSI_BLUE.concat("-------Guia 1 - Ejericio 7--------").concat(Utils.ANSI_RESET) );
				Guia1_Ej7();
				break;
			}
			case 8: {
				Utils.getOut().println( Utils.ANSI_BLUE.concat("-------Guia 1 - Ejericio 8--------").concat(Utils.ANSI_RESET) );
				Guia1_Ej8();
				break;
			}
			case 9: {
				Utils.getOut().println( Utils.ANSI_BLUE.concat("-------Guia 1 - Ejericio 9--------").concat(Utils.ANSI_RESET) );
				Guia1_Ej9();
				break;
			}
			case 10: {
				Utils.getOut().println( Utils.ANSI_BLUE.concat("-------Guia 1 - Ejericio 10--------").concat(Utils.ANSI_RESET) );
				Guia1_Ej10();
				break;
			}
			case 11: {
				Utils.getOut().println( Utils.ANSI_BLUE.concat("-------Guia 1 - Ejericio 11--------").concat(Utils.ANSI_RESET) );
				Guia1_Ej11();
				break;
			}
			case 12: {
				Utils.getOut().println( Utils.ANSI_BLUE.concat("-------Guia 1 - Ejericio 12--------").concat(Utils.ANSI_RESET) );
				Guia1_Ej12();
				break;
			}
			case 13: {
				Utils.getOut().println( Utils.ANSI_BLUE.concat("-------Guia 1 - Ejericio 13--------").concat(Utils.ANSI_RESET) );
				Guia1_Ej13();
				break;
			}
			case 14: {
				Utils.getOut().println( Utils.ANSI_BLUE.concat("-------Guia 1 - Ejericio 14--------").concat(Utils.ANSI_RESET) );
				Guia1_Ej14();
				break;
			}
			case 15: {
				Utils.getOut().println( Utils.ANSI_BLUE.concat("-------Guia 1 - Ejericio 15--------").concat(Utils.ANSI_RESET) );
				Guia1_Ej15();
				break;
			}
			default:
				Utils.getOut().println( Utils.ANSI_RED.concat("Opcion Invalida.").concat(Utils.ANSI_RESET) );
			}
		}
	}
	
	public void mostrarOpciones() {
		Utils.getOut().println(Utils.ANSI_GREEN.concat("===============================") );
		Utils.getOut().println("========Menu Principal=========");
		Utils.getOut().println("===============================".concat(Utils.ANSI_RESET) );
		
		Utils.getOut().println("\t 1. Guia 1 - Ejercicio 1");
		Utils.getOut().println("\t 2. Guia 1 - Ejercicio 2");
		Utils.getOut().println("\t 3. Guia 1 - Ejercicio 3");
		Utils.getOut().println("\t 4. Guia 1 - Ejercicio 4");
		Utils.getOut().println("\t 5. Guia 1 - Ejercicio 5");
		Utils.getOut().println("\t 6. Guia 1 - Ejercicio 6");
		Utils.getOut().println("\t 7. Guia 1 - Ejercicio 7");
		Utils.getOut().println("\t 8. Guia 1 - Ejercicio 8");
		Utils.getOut().println("\t 9. Guia 1 - Ejercicio 9");
		Utils.getOut().println("\t 10. Guia 1 - Ejercicio 10");
		Utils.getOut().println("\t 11. Guia 1 - Ejercicio 11");
		Utils.getOut().println("\t 12. Guia 1 - Ejercicio 12");
		Utils.getOut().println("\t 13. Guia 1 - Ejercicio 13");
		Utils.getOut().println("\t 14. Guia 1 - Ejercicio 14");
		Utils.getOut().println("\t 15. Guia 1 - Ejercicio 15");
		Utils.getOut().println("\t 0. Salir \n");
	}
	
	public int pedirNumero() {
		Utils.getOut().print("Ingrese una opcion:");
		Utils.getOut().flush();
		
		try {
			String linea = Utils.getLector().readLine();
			return Integer.valueOf( linea );
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public void Guia1_Ej1() {
		try {
			Utils.getOut().print("organizacion: ");
			String org = Utils.getLector().readLine();
			String[] palabras = org.split(" ");
			String acr = "";
			for (String p : palabras)
				acr += p.substring(0,1).toUpperCase();
			Utils.getOut().println(acr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	public void Guia1_Ej2() {
		try {
			Utils.getOut().print("palabra: ");
			String p = Utils.getLector().readLine();
			String inv = new StringBuilder(p).reverse().toString();
			Utils.getOut().println(p.equals(inv) ? "palindromo" : "no es");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	
	
	
	public void Guia1_Ej3() {
		try {
			Utils.getOut().print("oracion: ");
			String f = Utils.getLector().readLine().toLowerCase();
			int cont = 0;
			for (int i = 0; i < f.length(); i++)
				if ("aeiou".indexOf(f.charAt(i)) != -1)
					cont++;
			Utils.getOut().println(cont);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	
	
	
	public void Guia1_Ej4() {
		try {
			Utils.getOut().print("parrafo: ");
			String p = Utils.getLector().readLine();
			Utils.getOut().print("buscar: ");
			String b = Utils.getLector().readLine();
			Utils.getOut().print("reemplazo: ");
			String r = Utils.getLector().readLine();
			Utils.getOut().println(p.replace(b, r));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	
	
	
	
	public void Guia1_Ej5() {
		try {
			Utils.getOut().print("correo: ");
			String c = Utils.getLector().readLine();
			String user = c.split("@")[0];
			user = user.substring(0,1).toUpperCase() + user.substring(1).toLowerCase();
			Utils.getOut().println("bienvenido " + user);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	public void Guia1_Ej6() {
		try {
			Utils.getOut().print("texto: ");
			String t = Utils.getLector().readLine();
			t = t.trim().replaceAll("\\s+", " ");
			Utils.getOut().println(t);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	
	
	
	/*
	 * Ejercicio 7:  Lee una contraseña ingresada por consola. El sistema debe validarla aplicando tres 
	 * reglas: debe tener un mínimo de 8 caracteres de longitud, debe contener al menos un número, 
	 * y no debe contener la palabra "clave" en ninguna parte. 
	 * Imprime un mensaje indicando si la contraseña es segura o vulnerable.
	*/
	public void Guia1_Ej7() {
		Utils.getOut().print("\tIngrese el password:");
		Utils.getOut().flush();
		
		try {
			String psw = Utils.getLector().readLine();
			
			boolean longitudValida   = psw.length() >= 8;
			boolean contientePalabra = psw.contains("clave");
			boolean contieneNumeros = false;
			
			//boolean contieneNumeros = psw.matches( ".*\\d.*" ); //expresion regulares   "[a-zA-Z0-9_+&*-]*@"
			for(int i =0 ; i<=9 ; i++)
			{
				if( psw.contains( String.valueOf(i) ) == true )
				{
					contieneNumeros= true;
				}
			}
				
			if(longitudValida && contieneNumeros && !contientePalabra)
			{
				Utils.getOut().println("\tPassword SEGURO");
			}else {
				Utils.getOut().println("\tVULNERABLE");
			}
			Utils.getOut().println();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
	public void Guia1_Ej8() {
		try {
			Utils.getOut().print("ruta: ");
			String r = Utils.getLector().readLine();
			String ext = r.substring(r.lastIndexOf(".") + 1);
			Utils.getOut().println(ext);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	
	/* Ejercicio 9:  Solicita una palabra o frase corta. El código debe transformar 
	//el texto para que las letras alternen estrictamente entre mayúsculas y minúsculas 
	una por una. Muestra el resultado final impreso en la consola.
	*/
	
	public void Guia1_Ej9() {
		try {
			Utils.getOut().println("Ingrese una frase de 3 palabras");
			String linea = Utils.getLector().readLine();
			String textoFinal="";
			
			for(int i=0 ; i< linea.length() ; i++) {
				if( i%2==0 )
				{	
					textoFinal = textoFinal.concat( String.valueOf(linea.charAt(i)).toUpperCase() );
				}else{
					textoFinal = textoFinal.concat( String.valueOf(linea.charAt(i)).toLowerCase() );
				}
			}
			
			Utils.getOut().println(textoFinal);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	public void Guia1_Ej10() {
		try {
			Utils.getOut().print("texto: ");
			String t = Utils.getLector().readLine();
			Utils.getOut().print("fragmento: ");
			String f = Utils.getLector().readLine();
			int ini = t.indexOf(f);
			if (ini != -1)
				Utils.getOut().println("inicio: " + ini + " fin: " + (ini + f.length() - 1));
			else
				Utils.getOut().println("no encontrado");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	/*
	 * Ejercicio 11:  Solicita tres datos al usuario en diferentes lecturas: 
	 * el nombre de un producto (texto), la cantidad comprada (número entero) y 
	 * el precio unitario (número decimal). Utiliza el constructor de cadenas para ir 
	 * uniendo todos estos tipos de datos en un solo renglón con un formato legible que 
	 * simule un recibo, conviértelo a texto definitivo y muéstralo.
	 */
	public void Guia1_Ej11() {
		try {
			Utils.getOut().print("Nombre Producto:"); Utils.getOut().flush();
			String v1 = Utils.getLector().readLine();
			
			Utils.getOut().print("Cant:"); Utils.getOut().flush();
			int v2 =  Integer.parseInt( Utils.getLector().readLine() );
			
			Utils.getOut().print("Precio:"); Utils.getOut().flush();
			float v3 = Float.parseFloat( Utils.getLector().readLine() );
			
			StringBuilder st = new StringBuilder();
			st.append("Producto: ").append(v1);
			st.append(" | Cant:").append(v2);
			st.append(" | Precio Unit:").append( String.format("%.2f",v3)  );
			st.append(" | Precio Final:").append( (float)(v2 * v3) );
			Utils.getOut().println( st.toString() );
			
			Utils.getOut().printf("Producto:%s | Cant:%d | precio Unit:%.2f | Precio Final:%.1f %n", 
									        v1      , v2             , v3             , v2*v3);
			Utils.getOut().flush();
		}catch(IOException ex){
			
		}
	}
	
	
	public void Guia1_Ej12() {
		try {
			String nombre = Utils.getLector().readLine();
			StringBuilder st = new StringBuilder(nombre);
			st.insert(0, "Dr./Dra. ");
			Utils.getOut().println(st);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	public void Guia1_Ej13() {
		try {
			String o = Utils.getLector().readLine();
			int ini = o.indexOf("error");
			if (ini != -1) {
				int fin = ini + 5;
				if (fin < o.length() && o.charAt(fin) == ' ') fin++;
				Utils.getOut().println(o.substring(0, ini) + o.substring(fin));
			} else {
				Utils.getOut().println(o);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	public void Guia1_Ej14() {
		try {
			String p = Utils.getLector().readLine();
			StringBuilder st = new StringBuilder(p);
			st.insert(0, "<b>");
			st.append("</b>");
			Utils.getOut().println(st);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	
	public void Guia1_Ej15() {
		try {
			String p1 = Utils.getLector().readLine();
			String p2 = Utils.getLector().readLine();
			String p3 = Utils.getLector().readLine();
			Utils.getOut().println(p1 + "-" + p3);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

}














