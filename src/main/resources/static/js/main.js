/* =========================================================
   ODYXS — main.js (vanilla, sin dependencias de plugins)
   ========================================================= */
(function () {
    "use strict";

    // ── Spinner: ocultar en cuanto el DOM esté listo ──────────
    var hideSpinner = function () {
        var s = document.getElementById('spinner');
        if (s) s.classList.remove('show');
    };
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', hideSpinner);
    } else {
        hideSpinner();
    }
    window.addEventListener('load', hideSpinner);

    // ── Header liquid-glass: estado "scrolled" + back-to-top ──
    var navbar = document.querySelector('.navbar');
    var backTop = document.querySelector('.back-to-top');

    var onScroll = function () {
        var y = window.scrollY || document.documentElement.scrollTop;

        if (navbar) {
            navbar.classList.toggle('sticky-top', y > 45);
            navbar.classList.toggle('is-scrolled', y > 45);
        }
        if (backTop) {
            backTop.classList.toggle('back-to-top--visible', y > 300);
        }
    };

    window.addEventListener('scroll', onScroll, { passive: true });
    onScroll();

    // Back to top con scroll suave nativo
    if (backTop) {
        backTop.addEventListener('click', function (e) {
            e.preventDefault();
            window.scrollTo({ top: 0, behavior: 'smooth' });
        });
    }
})();
