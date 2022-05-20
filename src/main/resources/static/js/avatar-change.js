$(document).ready(function () {
    let input = document.querySelector('.custom-file-input'),
        label = document.querySelector('.custom-file-label'),
        labelVal = label.innerHTML;
    input.addEventListener('change', function (e) {
        let fileName = e.target.value.split('\\').pop();
        if (fileName)
            label.innerHTML = fileName;
        else
            label.innerHTML = labelVal;
    });
});