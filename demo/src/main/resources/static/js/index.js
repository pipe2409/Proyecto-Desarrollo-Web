
  const navbar = document.getElementById("navbar");
  let lastScrollY = window.scrollY;

  function handleNavbarVisibility() {
    const currentScrollY = window.scrollY;

    if (currentScrollY > 80) {
      navbar.classList.add("show-navbar");
      navbar.classList.add("scrolled");
    } else {
      navbar.classList.remove("show-navbar");
      navbar.classList.remove("scrolled");
    }

    lastScrollY = currentScrollY;
  }

  window.addEventListener("scroll", handleNavbarVisibility);
  window.addEventListener("load", handleNavbarVisibility);