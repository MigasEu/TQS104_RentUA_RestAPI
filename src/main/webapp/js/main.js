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
});