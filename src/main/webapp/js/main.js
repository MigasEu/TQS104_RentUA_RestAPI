$(document).ready(function () {
    $(document).on("click", "#info_editar", function () {
        $("#info_fname").prop("readonly", false);
        $("#info_lname").prop("readonly", false);
        $("#info_tel").prop("readonly", false);

        $("#info_editar").hide();
        $("#info_guardar").show();
        $("#info_cancelar").show();
        $
    });
    
    $(document).on("click", "#info_cancelar", function () {
        $("#info_fname").prop("readonly", true);
        $("#info_lname").prop("readonly", true);
        $("#info_tel").prop("readonly", true);

        $("#info_editar").show();
        $("#info_guardar").hide();
        $("#info_cancelar").hide();
    });
    
    $(document).on("change", "#filterby", function () {
       value = $(this).val();
       
       if (value == 'price') {
           $("#filter_price").show();
           $("#filter_price_range_from").hide();
           $("#filter_price_range_to").hide();
       }
       
       else if (value == 'price_range') {
           $("#filter_price").hide();
           $("#filter_price_range_from").show();
           $("#filter_price_range_to").show();
       }
       
       else {
           $("#filter_price").hide();
           $("#filter_price_range_from").hide();
           $("#filter_price_range_to").hide();
       }
    });
});