package com.example.istateca.ui.listado_prestamos;


import static com.example.istateca.V_principal.bibliotecario_ingresado;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.istateca.Clases.Libro;
import com.example.istateca.Clases.Prestamo;
import com.example.istateca.R;
import com.example.istateca.Utils.Apis;
import com.example.istateca.Utils.LibroService;
import com.example.istateca.Utils.PrestamoService;
import com.example.istateca.databinding.FragmentLisatdoPrestamosBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class lisatdo_prestamosFragment extends Fragment implements SearchView.OnQueryTextListener {
    LibroService libroService;
    PrestamoService prestamoService;
    public static List<Prestamo> prestamo;
    List<Libro> lista_libro= new ArrayList<>();
    ListView recyclerView ;
    public static int idPrestamo=0;
    int id_entrega=0;
    String fec ="";
    private LisatdoPrestamosViewModel mViewModel;
    private FragmentLisatdoPrestamosBinding binding;
    Dialog dialogo;
    ImageView editar;
    List<Prestamo> lista_prestamobuscar= new ArrayList<>();
    List<Prestamo> lista_prestamo= new ArrayList<>();
    public static lisatdo_prestamosFragment newInstance() {
        return new lisatdo_prestamosFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLisatdoPrestamosBinding.inflate(inflater, container, false);

        id_entrega=bibliotecario_ingresado.getId();
        View root = binding.getRoot();
        recyclerView= binding.listaPrestamo;
        listarPrestamoSolicitud("solicitado");

        //buscar prestamo
        binding.txtbuscar.setOnQueryTextListener(this);

        //Dialogo del detalle del libro
        dialogo=new Dialog(getActivity());
        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialogo.setContentView(R.layout.dialogo_detalle_solicitud);
                System.out.println("dialogo " + i);
                idPrestamo=prestamo.get(i).getId_prestamo();
                datos(dialogo,i);
                TextView txtcerrar=(TextView) dialogo.findViewById(R.id.txt_cerrar_detalle);
                txtcerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogo.dismiss();
                    }
                });
                dialogo.show();
            }
        });
        return root;
    }


    /*public void listarPrestamo(){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(Apis.URL_001)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        prestamoService=retrofit.create(PrestamoService.class);
        Call<List<Prestamo>> call= prestamoService.getListarPrestamo();
        call.enqueue(new Callback<List<Prestamo>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<Prestamo>> call, Response<List<Prestamo>> response) {
                if (!response.isSuccessful()){
                    Log.e("Response err: ",response.message());
                    return;
                }
                prestamo=response.body();
                lista_prestamoAdapter lista_prestamoAdapter= new lista_prestamoAdapter(prestamo,getActivity());
                recyclerView.setAdapter(lista_prestamoAdapter);
                prestamo.forEach(p-> System.out.println(prestamo.toString()));
            }

            @Override
            public void onFailure(Call<List<Prestamo>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }*/
    public void listarPrestamoSolicitud(String Solicitado){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(Apis.URL_001)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        prestamoService=retrofit.create(PrestamoService.class);
        Call<List<Prestamo>> call= prestamoService.getBuscarestado(Solicitado);
        call.enqueue(new Callback<List<Prestamo>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<Prestamo>> call, Response<List<Prestamo>> response) {
                if (!response.isSuccessful()){
                    Log.e("Response err: ",response.message());
                    return;
                }
                prestamo=response.body();
                lista_prestamoAdapter lista_prestamoAdapter= new lista_prestamoAdapter(prestamo,getActivity());
                recyclerView.setAdapter(lista_prestamoAdapter);
                prestamo.forEach(p-> System.out.println(prestamo.toString()));
            }

            @Override
            public void onFailure(Call<List<Prestamo>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void datos(Dialog dialog,int i){
        TextView txtcedula=dialog.findViewById(R.id.text_cedulaestudiante);
        TextView txtcodigo =dialog.findViewById(R.id.text_codigo);
        TextView txttitulo = dialog.findViewById(R.id.text_libro);
        TextView txtbibliotecario = dialog.findViewById(R.id.text_bibliotecario_entrega);
        TextView txtestado = dialog.findViewById(R.id.text_estado);
        Button btnfecha =dialog.findViewById(R.id.btn_capturaf);
        TextView txt_devolucionfecha=dialog.findViewById(R.id.txt_fechaMaximaDev_solicitud5);
        Button btnguardad = dialog.findViewById(R.id.btnGuardarSolicitud6);
        txtcedula.setText(prestamo.get(i).getUsuario().getPersona().getCedula() + "-" + prestamo.get(i).getUsuario().getPersona().getNombres());
        txtcodigo.setText(prestamo.get(i).getLibro().getCodigo_dewey());
        txttitulo.setText(prestamo.get(i).getLibro().getTitulo());
        txtestado.setText(prestamo.get(i).getLibro().getEstado_libro());
        txtbibliotecario.setText(bibliotecario_ingresado.getPersona().getNombres());

        btnfecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date());
                btnfecha.setText(d);
            }
        });

        txt_devolucionfecha.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int dia=c.get(Calendar.DAY_OF_MONTH);
                int  mes=c.get(Calendar.MONTH);
                int ano=c.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(btnguardad.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        txt_devolucionfecha.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                         fec= String.valueOf(txt_devolucionfecha);
                        System.out.println("fecha"+ txt_devolucionfecha);
                    }
                }
                        ,ano,mes,dia);

                datePickerDialog.show();
                System.out.println("fecha"+ txt_devolucionfecha);
            }

        });

        btnguardad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    public int buscarLibroxnombre(int id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Apis.URL_001)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        prestamoService = retrofit.create(PrestamoService.class);
        Call<Prestamo> call = prestamoService.updatePersona(id);
        call.enqueue(new Callback<Prestamo>() {
            @Override
            public void onResponse(Call<Prestamo> call, Response<Prestamo> response) {
                if (!response.isSuccessful()) {
                    Log.e("Response err: ", response.message());
                    return;
                }
               // lista_prestamobuscar= response.body();
                lista_prestamoAdapter lista_prestamoAdapter= new lista_prestamoAdapter(lista_prestamobuscar,getActivity());
                recyclerView.setAdapter(lista_prestamoAdapter);


            }

            @Override
            public void onFailure(Call<Prestamo> call, Throwable t) {
                Log.e("Error:",t.getMessage());
                System.out.println("error");
            }
        });

        return id;
    }
    private void prestamoSolicitud(){
        Response<List<Prestamo>> response = null;
        for (int i=0; i< prestamo.size(); i++){
            if (prestamo.get(i).getEstado_prestamo().equalsIgnoreCase("Solicitado")){

                lista_prestamo= response.body();
                System.out.println("Prestamo" + lista_prestamo.size());
                lista_prestamoAdapter lista_prestamoAdapter= new lista_prestamoAdapter(lista_prestamo,getActivity());
                recyclerView.setAdapter(lista_prestamoAdapter);
            }
        }
    }
    private void listarLibros(){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(Apis.URL_001)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        libroService=retrofit.create(LibroService.class);
        Call<List<Libro>> call= libroService.getListarLibros();
        call.enqueue(new Callback<List<Libro>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<Libro>> call, Response<List<Libro>> response) {
                if (!response.isSuccessful()){
                    Log.e("Response err: ",response.message());
                    return;
                }
                lista_libro=response.body();
                System.out.println(lista_libro.size() + " libros");
            }

            @Override
            public void onFailure(Call<List<Libro>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}