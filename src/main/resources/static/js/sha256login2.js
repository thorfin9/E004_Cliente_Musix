function calcularSha256() {
    const texto = document.getElementById('pass_sin_hash').value;

    if (texto === '') {
        document.getElementById('pass').value = '';
        return;
    }

    // Calcular el hash SHA-256 usando CryptoJS
    const hash = CryptoJS.SHA256(texto + 'Galea'); // pimienta (la misma que usa la API)

    
    document.getElementById('pass').value = hash.toString(CryptoJS.enc.Hex);
}