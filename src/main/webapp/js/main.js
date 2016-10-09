/**
 * Created by Aleksandar on 08-Oct-16.
 */
$('.reference-link').click(function () {
    var icon = $(this).find('i');
    // setTimeout(changeChevronIcon, 300, icon);
    changeChevronIcon(icon)
});

function changeChevronIcon(icon) {
    if (icon.hasClass('fa-chevron-down')) {
        icon.addClass('fa-chevron-up').removeClass('fa-chevron-down')
    } else if (icon.hasClass('fa-chevron-up')) {
        icon.addClass('fa-chevron-down').removeClass('fa-chevron-up')
    }
}