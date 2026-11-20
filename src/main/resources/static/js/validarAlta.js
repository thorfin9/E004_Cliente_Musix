
function validarAlta() {
    const p1 = document.getElementById('pass_sin_hash').value;
    const p2 = document.getElementById('repetir').value;

    if (p1 !== p2) {
        alert('Las contraseñas no coinciden');
        return false;
    }

    
    const elegidas = Array.from(document.querySelectorAll('.selectPregunta')).map(s => s.value);
    if (new Set(elegidas).size !== elegidas.length) {
        alert('Tienes que elegir 3 preguntas distintas');
        return false;
    }

    // la foto no puede pasar de 5MB
    const foto = document.getElementById('archivoFoto').files[0];
    if (foto && foto.size > 5 * 1024 * 1024) {
        alert('La foto no puede pesar más de 5MB');
        return false;
    }

   
    calcularSha256();
    return true;
}