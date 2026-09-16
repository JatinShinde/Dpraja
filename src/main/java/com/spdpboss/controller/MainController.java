package com.spdpboss.controller;

import com.spdpboss.model.*;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Locale;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import com.spdpboss.repository.*;
import com.spdpboss.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.file.*;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Controller
public class MainController {

	@Autowired
	private WeeklyChartRepository weeklyChartRepository;
	@Autowired
	private ResultService resultService;
	@Autowired
	private ResultRepository resultRepository;
	@Autowired
	private TodayFinalRepository todayFinalRepository;
	@Autowired
	private GoldenAnkRepository goldenAnkRepository;
	@Autowired
	private PaperRepository paperRepository;
	@Autowired
	private FreeAnkRepository freeAnkRepository;
	@Autowired
	private MarketAgentRepository marketAgentRepository;
	@Autowired
	private GameHistoryRepository historyRepository;
	@Autowired
	private GameRecordRepository gameRecordRepository;
	@Autowired
	private BidRepository bidRepository;
	@Autowired
	private UserWalletRepository userWalletRepository;

	private final String UPLOAD_DIR = "uploads/";

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("IST"));
		try {
			Files.createDirectories(Paths.get(UPLOAD_DIR));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/bidding")
	public String biddingPage(Model model) {
		model.addAttribute("allResults", resultRepository.findAllByOrderBySerialNoAsc());
		return "bidding";
	}

	@GetMapping("/")
	public String index(Model model) {
		List<Result> allResults = resultRepository.findAllByOrderBySerialNoAsc();
		model.addAttribute("allResults", allResults);

		// 1. Fetch all papers from the database
		List<Paper> allPapers = paperRepository.findAll();
		model.addAttribute("papers", allPapers);

		List<Result> liveResults = allResults.stream()
				.filter(Objects::nonNull) // Remove null objects
				.filter(this::isMarketLive) // Apply your timing logic
				.collect(Collectors.toList());
		model.addAttribute("liveResults", liveResults);
		model.addAttribute("allFreeAnks", freeAnkRepository.findAll());
		model.addAttribute("freeAnkDate", todayFinalRepository.findById(16L).map(TodayFinal::getContent).orElse(""));
		model.addAttribute("currentDate", java.time.LocalDate.now());

		// 2. Add them to the model so HTML can see them
		model.addAttribute("papers", allPapers);

		// Golden Ank (Ensure .getValue() exists)
		model.addAttribute("goldenAnkValue", goldenAnkRepository.findById(1L).map(g -> g.getValue()).orElse("00"));

		// Today Finals (Ensure .getContent() exists)
		model.addAttribute("punaFinal", todayFinalRepository.findById(1L).map(t -> t.getContent()).orElse("."));
		model.addAttribute("kalyanFinal", todayFinalRepository.findById(2L).map(t -> t.getContent()).orElse("."));
		model.addAttribute("mainFinal", todayFinalRepository.findById(3L).map(t -> t.getContent()).orElse("."));

		// Notice (Top)
		TodayFinal topN = todayFinalRepository.findById(4L).orElseGet(() -> {
			TodayFinal d = new TodayFinal(4L, "अगर आप खुद का मटका बाजार चलते हैं ....और अपने बाजार का रिजल्ट हमारी वेबसाइट पर डलवाना चाहते हैं..तो आज ही हमसे संपर्क करें :-\n\n• मात्र 5 मिनट में आपके बाजार का रिजल्ट वेबसाइट पर डाल दिया जायेगा |\n• आपका खुद का एडमिन पैनल बनाया जायेगा — जिससे आप खुद अपने बाजार का रिजल्ट अपडेट कर सकेंगे |\n• आपके बाजार का पुराना रिकॉर्ड भी वेबसाइट पर डाल दिया जायेगा |");
			todayFinalRepository.save(d);
			return d;
		});
		if ("No new notice.".equals(topN.getContent()) || "".equals(topN.getContent()) || topN.getContent() == null) {
			topN.setContent("अगर आप खुद का मटका बाजार चलते हैं ....और अपने बाजार का रिजल्ट हमारी वेबसाइट पर डलवाना चाहते हैं..तो आज ही हमसे संपर्क करें :-\n\n• मात्र 5 मिनट में आपके बाजार का रिजल्ट वेबसाइट पर डाल दिया जायेगा |\n• आपका खुद का एडमिन पैनल बनाया जायेगा — जिससे आप खुद अपने बाजार का रिजल्ट अपडेट कर सकेंगे |\n• आपके बाजार का पुराना रिकॉर्ड भी वेबसाइट पर डाल दिया जायेगा |");
			todayFinalRepository.save(topN);
		}
		model.addAttribute("topNoticeContent", topN.getContent());
		model.addAttribute("topNoticeHidden", topN.isHidden());

		// Notice (Middle)
		TodayFinal middleN = todayFinalRepository.findById(12L).orElse(new TodayFinal(12L, "For Advertisement & Inquiries: dpraja1@hotmail.com"));
		model.addAttribute("middleNoticeContent", middleN.getContent());
		model.addAttribute("middleNoticeHidden", middleN.isHidden());

		// Notice (Bottom)
		TodayFinal bottomN = todayFinalRepository.findById(11L).orElse(new TodayFinal(11L, "No new notice."));
		model.addAttribute("bottomNoticeContent", bottomN.getContent());
		model.addAttribute("bottomNoticeHidden", bottomN.isHidden());

		// Free Detailed
		model.addAttribute("punaFreeDetailed",
				todayFinalRepository.findById(8L).map(TodayFinal::getContent).orElse("..."));
		model.addAttribute("kalyanFreeDetailed",
				todayFinalRepository.findById(9L).map(TodayFinal::getContent).orElse("..."));
		model.addAttribute("mainFreeDetailed",
				todayFinalRepository.findById(10L).map(TodayFinal::getContent).orElse("..."));

		model.addAttribute("currentDate", LocalDate.now());
		model.addAttribute("dailyPapers", paperRepository.findByType("DAILY"));

		// Fetch Weekly Chart Data (creates empty default if none exists)
		WeeklyChart weeklyChart = weeklyChartRepository.findById(1L).orElse(new WeeklyChart());
		model.addAttribute("weeklyChart", weeklyChart);

		// Dynamic Contact Us Email (Record ID 15)
		TodayFinal contactEmailRecord = todayFinalRepository.findById(15L).orElseGet(() -> {
			TodayFinal d = new TodayFinal(15L, "dpraja1@hotmail.com");
			todayFinalRepository.save(d);
			return d;
		});
		model.addAttribute("contactEmail", contactEmailRecord.getContent());

		return "index";
	}

	@GetMapping({"/admin", "/dashboard"})
	public String showAdminDashboard(
			@RequestParam(value = "section", required = false, defaultValue = "home") String section, Model model) {
		List<Result> allResults = resultRepository.findAllByOrderBySerialNoAsc();
		List<Result> liveResults = allResults.stream()
				.filter(this::isMarketLive)
				.collect(Collectors.toList());

		model.addAttribute("liveResults", liveResults);

		// Fix: Filter allResults, not allGames
		allResults.removeIf(game -> game == null || game.getId() == null);

		// Bids & Wallets Stats for Dashboard
		List<Bid> allBids = bidRepository.findAll();
		List<UserWallet> allWallets = userWalletRepository.findAll();
		long pendingBidsCount = allBids.stream().filter(b -> "PENDING".equals(b.getStatus())).count();
		double totalWalletBalance = allWallets.stream().mapToDouble(UserWallet::getBalance).sum();

		model.addAttribute("allBids", allBids);
		model.addAttribute("allWallets", allWallets);
		model.addAttribute("totalBidsCount", allBids.size());
		model.addAttribute("pendingBidsCount", pendingBidsCount);
		model.addAttribute("totalWalletsCount", allWallets.size());
		model.addAttribute("totalWalletBalance", totalWalletBalance);

		// Fix: Pass allResults to the model
		model.addAttribute("allResults", allResults);
		model.addAttribute("currentSection", section);
		model.addAttribute("currentDate", LocalDate.now());

		// Always populate section data so all 16 navigation tabs work seamlessly
		model.addAttribute("allPapers", paperRepository.findAll());

		GoldenAnk goldenAnk = goldenAnkRepository.findById(1L).orElseGet(() -> {
			GoldenAnk newAnk = new GoldenAnk();
			newAnk.setId(1L);
			return newAnk;
		});
		model.addAttribute("goldenAnk", goldenAnk);

		TodayFinal topN = todayFinalRepository.findById(4L).orElseGet(() -> {
			TodayFinal d = new TodayFinal(4L, "अगर आप खुद का मटका बाजार चलते हैं ....और अपने बाजार का रिजल्ट हमारी वेबसाइट पर डलवाना चाहते हैं..तो आज ही हमसे संपर्क करें :-\n\n• मात्र 5 मिनट में आपके बाजार का रिजल्ट वेबसाइट पर डाल दिया जायेगा |\n• आपका खुद का एडमिन पैनल बनाया जायेगा — जिससे आप खुद अपने बाजार का रिजल्ट अपडेट कर सकेंगे |\n• आपके बाजार का पुराना रिकॉर्ड भी वेबसाइट पर डाल दिया जायेगा |");
			todayFinalRepository.save(d);
			return d;
		});
		TodayFinal middleNoticeRecord = todayFinalRepository.findById(12L).orElseGet(() -> {
			TodayFinal d = new TodayFinal(12L, "For Advertisement & Inquiries: dpraja1@hotmail.com");
			todayFinalRepository.save(d);
			return d;
		});
		TodayFinal bottomN = todayFinalRepository.findById(11L).orElse(new TodayFinal(11L, ""));
		model.addAttribute("topNotice", topN);
		model.addAttribute("middleNotice", middleNoticeRecord);
		model.addAttribute("bottomNotice", bottomN);

		// Dynamic Contact Us Email for Admin Edit Tab
		TodayFinal contactEmailRecord = todayFinalRepository.findById(15L).orElseGet(() -> {
			TodayFinal d = new TodayFinal(15L, "dpraja1@hotmail.com");
			todayFinalRepository.save(d);
			return d;
		});
		model.addAttribute("contactEmailRecord", contactEmailRecord);

		model.addAttribute("allFreeAnks", freeAnkRepository.findAll());
		model.addAttribute("freeAnkDate", todayFinalRepository.findById(16L).map(TodayFinal::getContent).orElse(""));
		model.addAttribute("punaFinal", todayFinalRepository.findById(1L).map(TodayFinal::getContent).orElse(""));
		model.addAttribute("kalyanFinal", todayFinalRepository.findById(2L).map(TodayFinal::getContent).orElse(""));
		model.addAttribute("mainFinal", todayFinalRepository.findById(3L).map(TodayFinal::getContent).orElse(""));

		model.addAttribute("freePuna", freeAnkRepository.findByName("PunaBazar").map(FreeAnk::getFinalAnk).orElse(""));
		model.addAttribute("freeKalyan", freeAnkRepository.findByName("Kalyan").map(FreeAnk::getFinalAnk).orElse(""));
		model.addAttribute("freeMain", freeAnkRepository.findByName("MainBazar").map(FreeAnk::getFinalAnk).orElse(""));

		WeeklyChart weeklyChart = weeklyChartRepository.findById(1L).orElse(new WeeklyChart());
		model.addAttribute("weeklyChart", weeklyChart);

		return "admin";
	}

	@PostMapping("/admin/update-contact-email")
	public String updateContactEmail(@RequestParam String email, RedirectAttributes redirectAttributes) {
		TodayFinal contact = todayFinalRepository.findById(15L).orElse(new TodayFinal(15L, ""));
		contact.setContent(email.trim());
		todayFinalRepository.save(contact);
		redirectAttributes.addFlashAttribute("successMsg", "Contact email updated successfully!");
		return "redirect:/admin?section=notice&success";
	}

	@PostMapping("/admin/change-password")
	public String changePassword(@RequestParam String newPassword, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("successMsg", "Password updated successfully!");
		return "redirect:/admin?section=change-password&success";
	}

	@Transactional
	@PostMapping("/admin/update-game-result")
	public String updateGameResult(
			@RequestParam("id") Long id,
			@RequestParam(value = "chartDate", required = false) String chartDate,
			@RequestParam(value = "openPanel", required = false) String openPanel,
			@RequestParam(value = "openAnk", required = false) String openAnk,
			@RequestParam(value = "closeAnk", required = false) String closeAnk,
			@RequestParam(value = "closePanel", required = false) String closePanel,
			@RequestParam(value = "openTime", required = false) String openTime,
			@RequestParam(value = "closeTime", required = false) String closeTime) {

		System.out.println("--- DEBUGGING SAVE ---");
		System.out.println("ID: " + id);
		System.out.println("Saving Panel: " + openPanel);
		System.out.println("Saving Time: " + openTime);

		if (openPanel != null && openPanel.contains("-")) {
			String[] parts = openPanel.trim().split("-");
			if (parts.length >= 1) {
				String p0 = parts[0].trim().replace("*", "");
				if (!p0.isEmpty()) openPanel = p0;
			}
			if (parts.length >= 2) {
				String p1 = parts[1].trim();
				String cleanP1 = p1.replace("*", "").trim();
				if (cleanP1.length() >= 2) {
					openAnk = String.valueOf(cleanP1.charAt(0));
					closeAnk = String.valueOf(cleanP1.charAt(1));
				} else if (cleanP1.length() == 1) {
					openAnk = String.valueOf(cleanP1.charAt(0));
				}
			}
			if (parts.length >= 3) {
				String p2 = parts[2].trim().replace("*", "");
				if (!p2.isEmpty()) closePanel = p2;
			}
		}

		Result game = resultRepository.findById(id).orElse(null);

		if (game != null) {

			if (chartDate != null && !chartDate.isEmpty())
				game.setChartDate(chartDate.trim());
			if (openPanel != null)
				game.setOpenPanel(openPanel.trim());
			if (openAnk != null)
				game.setOpenAnk(openAnk.trim());
			if (closeAnk != null)
				game.setCloseAnk(closeAnk.trim());
			if (closePanel != null)
				game.setClosePanel(closePanel.trim());

			if (openTime != null)
				game.setOpenTime(openTime.trim());
			if (closeTime != null)
				game.setCloseTime(closeTime.trim());

			// JODI GENERATION: Combine Anks if they both exist in the database
			String currentOpenAnk = (game.getOpenAnk() != null && !game.getOpenAnk().equals("*")) ? game.getOpenAnk() : "";
			String currentCloseAnk = (game.getCloseAnk() != null && !game.getCloseAnk().equals("*")) ? game.getCloseAnk() : "";
			if (!currentOpenAnk.isEmpty() && !currentCloseAnk.isEmpty()) {
				game.setJodi(currentOpenAnk + currentCloseAnk);
			} else if (!currentOpenAnk.isEmpty()) {
				game.setJodi(currentOpenAnk + "*");
			} else if (!currentCloseAnk.isEmpty()) {
				game.setJodi("*" + currentCloseAnk);
			} else {
				game.setJodi("**");
			}

			// Save exactly once
			Result savedGame = resultRepository.saveAndFlush(game);

			System.out.println("VERIFY DB SAVE: " + savedGame.getOpenTime());

			// Update/Insert GameHistory and GameRecord for history charts
			String gameName = game.getGameName().toUpperCase().trim();
			LocalDate dateObj = (chartDate != null && !chartDate.isEmpty()) ? LocalDate.parse(chartDate.trim())
					: LocalDate.now(ZoneId.of("Asia/Kolkata"));

			// 1. Update GameHistory (for web frontend template)
			GameHistory gameHistory = historyRepository.findByGameNameAndResultDate(gameName, dateObj);
			if (gameHistory == null) {
				gameHistory = new GameHistory();
				gameHistory.setGameName(gameName);
				gameHistory.setResultDate(dateObj);
			}
			gameHistory.setOpenPanel(game.getOpenPanel());
			gameHistory.setOpenAnk(game.getOpenAnk());
			gameHistory.setCloseAnk(game.getCloseAnk());
			gameHistory.setClosePanel(game.getClosePanel());
			gameHistory.setJodi(game.getJodi());
			historyRepository.save(gameHistory);

			// 2. Update GameRecord (for mobile app API)
			GameRecord record = gameRecordRepository.findByGameNameAndDate(gameName, dateObj);
			if (record == null) {
				record = new GameRecord();
				record.setGameName(gameName);
				record.setDate(dateObj);
			}
			record.setOpenPanel(game.getOpenPanel());
			record.setClosePanel(game.getClosePanel());
			record.setJodi(game.getJodi());

			String openPana = game.getOpenPanel();
			if (openPana != null && openPana.length() == 3) {
				record.setOpen1(openPana.substring(0, 1));
				record.setOpen2(openPana.substring(1, 2));
				record.setOpen3(openPana.substring(2, 3));
			} else {
				record.setOpen1("");
				record.setOpen2("");
				record.setOpen3("");
			}

			String closePana = game.getClosePanel();
			if (closePana != null && closePana.length() == 3) {
				record.setClose1(closePana.substring(0, 1));
				record.setClose2(closePana.substring(1, 2));
				record.setClose3(closePana.substring(2, 3));
			} else {
				record.setClose1("");
				record.setClose2("");
				record.setClose3("");
			}
			gameRecordRepository.save(record);
		}

		return "redirect:/admin?section=home";
	}

	@PostMapping("/admin/update-notice")
	public String updateNotice(
			@RequestParam Long id, 
			@RequestParam String content,
			@RequestParam(value = "hidden", defaultValue = "false") boolean hidden) {
		TodayFinal notice = todayFinalRepository.findById(id).orElse(new TodayFinal(id, ""));
		notice.setContent(content);
		notice.setHidden(hidden);
		todayFinalRepository.save(notice);
		return "redirect:/admin?section=notice&success";
	}

	@PostMapping("/admin/update-golden-ank")
	public String updateGoldenAnk(@RequestParam String value) {
		GoldenAnk ank = goldenAnkRepository.findById(1L).orElse(new GoldenAnk());
		ank.setId(1L);
		ank.setValue(value);
		goldenAnkRepository.save(ank);
		return "redirect:/admin?section=golden-ank&success";
	}

	@PostMapping("/admin/update-final-ank")
	public String updateFinalAnk(@RequestParam Long id, @RequestParam String content) {
		TodayFinal tf = todayFinalRepository.findById(id).orElse(new TodayFinal());
		tf.setId(id);
		tf.setContent(content);
		todayFinalRepository.save(tf);
		return "redirect:/admin?section=free-ank&success";
	}

	// 1. GET Mapping: Used to LOAD the form page
	@GetMapping("/admin/create-game")
	public String showCreateGamePage() {
		return "create-game";
	}

	// 2. POST Mapping: Used to SAVE the form data
	@PostMapping("/admin/create-game")
	public String createNewGame(
			@RequestParam("gameName") String gameName,
			@RequestParam(value = "openTime", required = false) String openTime,
			@RequestParam(value = "closeTime", required = false) String closeTime,
			@RequestParam(value = "serialNo", defaultValue = "0") int serialNo,
			@RequestParam(value = "marketDays", required = false) String marketDays,
			@RequestParam(value = "marketColor", required = false) String marketColor,
			@RequestParam(value = "days", required = false) List<String> days) {

		Result newGame = new Result();

		newGame.setGameName(gameName.toUpperCase().trim());
		if (openTime != null)
			newGame.setOpenTime(openTime.trim());
		if (closeTime != null)
			newGame.setCloseTime(closeTime.trim());

		newGame.setSerialNo(serialNo);
		if (marketDays != null)
			newGame.setMarketDays(marketDays.trim());
		if (marketColor != null)
			newGame.setMarketColor(marketColor.trim());

		if (days != null && !days.isEmpty()) {
			newGame.setDaysOfWeek(String.join(",", days));
		} else {
			newGame.setDaysOfWeek("");
		}

		newGame.setOpenPanel("");
		newGame.setJodi("");
		newGame.setClosePanel("");

		resultRepository.save(newGame);

		return "redirect:/admin?section=market-list";
	}

	@PostMapping("/admin/update-market")
	public String updateMarket(
			@RequestParam("id") Long id,
			@RequestParam("gameName") String gameName,
			@RequestParam(value = "openTime", required = false) String openTime,
			@RequestParam(value = "closeTime", required = false) String closeTime,
			@RequestParam(value = "openPanel", required = false) String openPanel,
			@RequestParam(value = "openAnk", required = false) String openAnk,
			@RequestParam(value = "closeAnk", required = false) String closeAnk,
			@RequestParam(value = "closePanel", required = false) String closePanel,
			@RequestParam(value = "serialNo", defaultValue = "0") int serialNo,
			@RequestParam(value = "marketDays", required = false) String marketDays,
			@RequestParam(value = "marketColor", required = false) String marketColor,
			@RequestParam(value = "days", required = false) List<String> days,
			@RequestParam(value = "openPreTime", required = false) Integer openPreTime,
			@RequestParam(value = "openPostTime", required = false) Integer openPostTime,
			@RequestParam(value = "closePreTime", required = false) Integer closePreTime,
			@RequestParam(value = "closePostTime", required = false) Integer closePostTime,
			@RequestParam(value = "redirectSection", defaultValue = "market-list") String redirectSection) {

		Result game = resultRepository.findById(id).orElse(null);

		if (game != null) {
			game.setGameName(gameName.toUpperCase().trim());

			if (openTime != null)
				game.setOpenTime(openTime.trim());
			if (closeTime != null)
				game.setCloseTime(closeTime.trim());

			if (openPanel != null)
				game.setOpenPanel(openPanel.trim());
			if (openAnk != null)
				game.setOpenAnk(openAnk.trim());
			if (closeAnk != null)
				game.setCloseAnk(closeAnk.trim());
			if (closePanel != null)
				game.setClosePanel(closePanel.trim());

			String currentOpenAnk = (game.getOpenAnk() != null) ? game.getOpenAnk() : "";
			String currentCloseAnk = (game.getCloseAnk() != null) ? game.getCloseAnk() : "";
			game.setJodi(currentOpenAnk + currentCloseAnk);

			game.setSerialNo(serialNo);

			if (marketDays != null)
				game.setMarketDays(marketDays);
			if (marketColor != null)
				game.setMarketColor(marketColor);

			if (days != null && !days.isEmpty()) {
				game.setDaysOfWeek(String.join(",", days));
			} else if (days != null) {
				game.setDaysOfWeek("");
			}

			if (openPreTime != null)
				game.setOpenPreTime(openPreTime);
			if (openPostTime != null)
				game.setOpenPostTime(openPostTime);
			if (closePreTime != null)
				game.setClosePreTime(closePreTime);
			if (closePostTime != null)
				game.setClosePostTime(closePostTime);

			resultRepository.save(game);

			String normGameName = game.getGameName().toUpperCase().trim();
			LocalDate todayIST = LocalDate.now(ZoneId.of("Asia/Kolkata"));
			GameHistory gameHistory = historyRepository.findByGameNameAndResultDate(normGameName, todayIST);
			if (gameHistory == null) {
				gameHistory = new GameHistory();
				gameHistory.setGameName(normGameName);
				gameHistory.setResultDate(todayIST);
			}
			gameHistory.setOpenPanel(game.getOpenPanel());
			gameHistory.setOpenAnk(game.getOpenAnk());
			gameHistory.setCloseAnk(game.getCloseAnk());
			gameHistory.setClosePanel(game.getClosePanel());
			gameHistory.setJodi(game.getJodi());
			historyRepository.save(gameHistory);
		}

		return "redirect:/admin?section=" + redirectSection + "&selectedId=" + id + "&success";
	}

	@PostMapping("/admin/add-paper")
	public String handleFileUpload(@RequestParam String title, @RequestParam String type,
			@RequestParam MultipartFile file) {
		try {
			String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
			Files.copy(file.getInputStream(), Paths.get(UPLOAD_DIR).resolve(fileName));
			Paper p = new Paper();
			p.setTitle(title);
			p.setType(type);
			p.setImageUrl("/uploads/" + fileName);
			paperRepository.save(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "redirect:/admin?section=papers&success";
	}

	@PostMapping("/admin/delete-paper")
	public String deletePaper(@RequestParam Long id) {
		paperRepository.deleteById(id);
		return "redirect:/admin?section=papers&deleted";
	}

	@PostMapping("/admin/delete-market/{id}")
	public String deleteMarket(@PathVariable Long id) {
		resultRepository.deleteById(id);
		return "redirect:/admin?section=market-list";
	}

	@GetMapping("/login")
	public String showLoginPage() {
		return "login";
	}

	@PostMapping("/login")
	public String handleLogin(@RequestParam String username, @RequestParam String password) {
		return ("admin".equals(username) && "admin123".equals(password)) ? "redirect:/admin" : "login";
	}

	@GetMapping("/logout")
	public String handleLogout() {
		return "redirect:/login";
	}

	@GetMapping("/jodi-chart")
	public String showJodiChart(@RequestParam String name, Model model) {
		Map<LocalDate, List<GameHistory>> weeks = buildWeeklyMap(name);

		Optional<Result> gameOpt = resultRepository.findByGameNameIgnoreCase(name.trim());
		List<String> activeDays = new ArrayList<>();
		String daysStr = null;
		if (gameOpt.isPresent()) {
			Result game = gameOpt.get();
			if (game.getMarketDays() != null && !game.getMarketDays().trim().isEmpty()) {
				daysStr = game.getMarketDays();
			} else if (game.getDaysOfWeek() != null && !game.getDaysOfWeek().trim().isEmpty()) {
				daysStr = game.getDaysOfWeek();
			}
		}
		if (daysStr != null && !daysStr.trim().isEmpty()) {
			activeDays = Arrays.stream(daysStr.split(",")).map(String::trim).collect(Collectors.toList());
		} else {
			activeDays = Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat");
		}

		model.addAttribute("weeks", weeks);
		model.addAttribute("gameName", name);
		model.addAttribute("activeDays", activeDays);
		return "jodi-chart";
	}

	@GetMapping("/panel-chart")
	public String showPanelChart(@RequestParam String name, Model model) {
		Map<LocalDate, List<GameHistory>> weeks = buildWeeklyMap(name);

		Optional<Result> gameOpt = resultRepository.findByGameNameIgnoreCase(name.trim());
		List<String> activeDays = new ArrayList<>();
		String daysStr = null;
		if (gameOpt.isPresent()) {
			Result game = gameOpt.get();
			if (game.getMarketDays() != null && !game.getMarketDays().trim().isEmpty()) {
				daysStr = game.getMarketDays();
			} else if (game.getDaysOfWeek() != null && !game.getDaysOfWeek().trim().isEmpty()) {
				daysStr = game.getDaysOfWeek();
			}
		}
		if (daysStr != null && !daysStr.trim().isEmpty()) {
			activeDays = Arrays.stream(daysStr.split(",")).map(String::trim).collect(Collectors.toList());
		} else {
			activeDays = Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat");
		}

		model.addAttribute("weeks", weeks);
		model.addAttribute("gameName", name);
		model.addAttribute("activeDays", activeDays);

		return "panel-chart";
	}

	private Map<LocalDate, List<GameHistory>> buildWeeklyMap(String name) {
		String normName = (name != null) ? name.trim().toUpperCase() : "";
		List<GameHistory> historyList = historyRepository.findByGameNameIgnoreCaseOrderByResultDateDesc(normName);

		// Group existing records by Monday of their week
		Map<LocalDate, List<GameHistory>> weeksMap = historyList.stream()
				.collect(Collectors.groupingBy(
						r -> r.getResultDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
						TreeMap::new,
						Collectors.toList()));

		LocalDate currentMonday = LocalDate.now(ZoneId.of("Asia/Kolkata")).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

		LocalDate startMonday;
		if (!weeksMap.isEmpty()) {
			LocalDate earliestRecordedMonday = weeksMap.keySet().iterator().next();
			startMonday = earliestRecordedMonday;
		} else {
			startMonday = currentMonday.minusWeeks(10);
		}

		LocalDate curr = startMonday;
		while (!curr.isAfter(currentMonday)) {
			weeksMap.putIfAbsent(curr, new ArrayList<>());
			curr = curr.plusWeeks(1);
		}

		return weeksMap;
	}

	@PostMapping("/admin/update-today-final")
	public String updateTodayFinal(@RequestParam String punaVal, @RequestParam String kalyanVal,
			@RequestParam String mainVal) {

		TodayFinal puna = todayFinalRepository.findById(1L).orElse(new TodayFinal(1L, punaVal));
		puna.setContent(punaVal.trim());
		todayFinalRepository.save(puna);

		TodayFinal kalyan = todayFinalRepository.findById(2L).orElse(new TodayFinal(2L, kalyanVal));
		kalyan.setContent(kalyanVal.trim());
		todayFinalRepository.save(kalyan);

		TodayFinal main = todayFinalRepository.findById(3L).orElse(new TodayFinal(3L, mainVal));
		main.setContent(mainVal.trim());
		todayFinalRepository.save(main);

		return "redirect:/admin?section=free-ank&success";
	}

	@PostMapping("/admin/update-free-ank-grid")
	public String updateFreeAnkGrid(
			@RequestParam(value = "freeAnkDate", required = false) String freeAnkDate,
			@RequestParam String punaVal, @RequestParam String kalyanVal,
			@RequestParam String mainVal) {

		if (freeAnkDate != null) {
			TodayFinal dateRecord = todayFinalRepository.findById(16L).orElse(new TodayFinal(16L, ""));
			dateRecord.setContent(freeAnkDate.trim());
			todayFinalRepository.save(dateRecord);
		}

		updateAnkValue("PunaBazar", punaVal);
		updateAnkValue("Kalyan", kalyanVal);
		updateAnkValue("MainBazar", mainVal);

		return "redirect:/admin?section=free-ank&success";
	}

	private void updateAnkValue(String name, String value) {
		FreeAnk ank = freeAnkRepository.findByName(name).orElse(new FreeAnk(name));
		ank.setFinalAnk(value);
		freeAnkRepository.save(ank);
	}

	@PostMapping("/admin/saveTiming")
	public String saveTiming(@RequestParam("id") Long id,
			@RequestParam("openPreTime") int openPreTime,
			@RequestParam("openPostTime") int openPostTime,
			@RequestParam("closePreTime") int closePreTime,
			@RequestParam("closePostTime") int closePostTime,
			RedirectAttributes redirectAttributes) {

		Result game = resultRepository.findById(id).orElse(null);
		if (game != null) {
			game.setOpenPreTime(openPreTime);
			game.setOpenPostTime(openPostTime);
			game.setClosePreTime(closePreTime);
			game.setClosePostTime(closePostTime);
			resultRepository.save(game);
		}
		return "redirect:/admin?section=market-timing";
	}

	private boolean isMarketLive(Result game) {
		if (game.getOpenTime() == null || game.getOpenTime().trim().isEmpty() ||
				game.getCloseTime() == null || game.getCloseTime().trim().isEmpty()) {
			return false;
		}

		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
			LocalTime now = LocalTime.now();

			LocalTime openTime = LocalTime.parse(game.getOpenTime().trim(), formatter);
			LocalTime closeTime = LocalTime.parse(game.getCloseTime().trim(), formatter);

			// Window 1: OPEN PANA
			LocalTime openStart = openTime.minusMinutes((long) game.getOpenPreTime());
			LocalTime openEnd = openTime.plusMinutes((long) game.getOpenPostTime());

			// Window 2: CLOSE PANA
			LocalTime closeStart = closeTime.minusMinutes((long) game.getClosePreTime());
			LocalTime closeEnd = closeTime.plusMinutes((long) game.getClosePostTime());

			// Check if we are currently inside either window
			boolean isLiveInOpenWindow = isTimeInWindow(now, openStart, openEnd);
			boolean isLiveInCloseWindow = isTimeInWindow(now, closeStart, closeEnd);

			return isLiveInOpenWindow || isLiveInCloseWindow;

		} catch (Exception e) {
			System.out.println("DEBUG ERROR [" + game.getGameName() + "]: " + e.getMessage());
			return false;
		}
	}

	private boolean isTimeInWindow(LocalTime now, LocalTime start, LocalTime end) {
		if (start.isBefore(end)) {
			return !now.isBefore(start) && !now.isAfter(end);
		} else {
			return !now.isBefore(start) || !now.isAfter(end);
		}
	}

	@PostMapping("/admin/update-weekly-charts")
	public String updateWeeklyCharts(
			@RequestParam(value = "ankChart", required = false) String ankChart,
			@RequestParam(value = "jodiChart", required = false) String jodiChart,
			@RequestParam(value = "pattiChart", required = false) String pattiChart) {

		WeeklyChart chart = weeklyChartRepository.findById(1L).orElse(new WeeklyChart());
		chart.setId(1L);
		chart.setAnkChart(ankChart);
		chart.setJodiChart(jodiChart);
		chart.setPattiChart(pattiChart);

		weeklyChartRepository.save(chart);

		return "redirect:/admin?section=weekly-update&success";
	}

	@GetMapping("/all-market-free-fix")
	public String allMarketFreeFix() {
		return "all-market-free-fix";
	}

	@GetMapping("/matka-jodi-count")
	public String matkaJodiCount() {
		return "matka-jodi-count";
	}

	@GetMapping("/dhanvarsha-daily-fix")
	public String dhanvarshaDailyFix() {
		return "dhanvarsha-daily-fix";
	}

	@GetMapping("/matka-jodi-family")
	public String matkaJodiFamily() {
		return "matka-jodi-family";
	}

	@GetMapping("/penal-count-chart")
	public String penalCountChart() {
		return "penal-count-chart";
	}

	@GetMapping("/penal-total-chart")
	public String penalTotalChart() {
		return "penal-total-chart";
	}

	@GetMapping("/all-220-card-list")
	public String all220CardList() {
		return "all-220-card-list";
	}

	@GetMapping("/matka-final-number-trick")
	public String matkaFinalNumberTrick() {
		return "matka-final-number-trick";
	}
}