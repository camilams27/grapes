import 'package:flutter/material.dart';
import '../core/theme/app_theme.dart';
import '../services/api_service.dart';
import 'register_screen.dart';
import 'home_screen.dart';

/// Tela de Login com estilo Pixel Art / Retrô
class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen>
    with SingleTickerProviderStateMixin {
  final _formKey = GlobalKey<FormState>();
  final _nicknameController = TextEditingController();
  final _passwordController = TextEditingController();
  final _apiService = ApiService();

  bool _isLoading = false;
  bool _obscurePassword = true;
  String? _errorMessage;

  late AnimationController _animationController;
  late Animation<double> _fadeAnimation;
  late Animation<Offset> _slideAnimation;

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 800),
    );

    _fadeAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(parent: _animationController, curve: Curves.easeOut),
    );

    _slideAnimation =
        Tween<Offset>(begin: const Offset(0, 0.3), end: Offset.zero).animate(
          CurvedAnimation(
            parent: _animationController,
            curve: Curves.easeOutCubic,
          ),
        );

    _animationController.forward();
  }

  @override
  void dispose() {
    _nicknameController.dispose();
    _passwordController.dispose();
    _animationController.dispose();
    super.dispose();
  }

  Future<void> _handleLogin() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final success = await _apiService.login(
        nickname: _nicknameController.text.trim(),
        password: _passwordController.text,
      );

      if (success && mounted) {
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (_) => const HomeScreen()),
        );
      } else {
        setState(() => _errorMessage = 'Falha no login. Tente novamente.');
      }
    } catch (e) {
      setState(() => _errorMessage = e.toString());
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  void _navigateToRegister() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (_) => const RegisterScreen()),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(
          image: DecorationImage(
            image: AssetImage('assets/images/background.png'),
            fit: BoxFit.cover,
            colorFilter: ColorFilter.mode(Color(0x99000000), BlendMode.darken),
          ),
        ),
        child: SafeArea(
          child: Center(
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(24),
              child: FadeTransition(
                opacity: _fadeAnimation,
                child: SlideTransition(
                  position: _slideAnimation,
                  child: Form(
                    key: _formKey,
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        // Logo / Título
                        _buildLogo(),
                        const SizedBox(height: 48),

                        // Card de Login
                        _buildLoginCard(),
                        const SizedBox(height: 24),

                        // Link para registro
                        _buildRegisterLink(),
                      ],
                    ),
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildLogo() {
    return Column(
      children: [
        // Ícone da uva pixel art
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: AppTheme.primaryPurple.withValues(alpha: 0.8),
            border: Border.all(color: AppTheme.neonGreen, width: 4),
            boxShadow: [
              BoxShadow(
                color: AppTheme.neonGreen.withValues(alpha: 0.4),
                blurRadius: 20,
                spreadRadius: 2,
              ),
            ],
          ),
          child: Image.asset(
            'assets/images/fgreeengraps.gif',
            width: 80,
            height: 80,
          ),
        ),
        const SizedBox(height: 16),

        // Título
        Text(
          'GRAPES',
          style: AppTheme.pixelTitle.copyWith(
            color: AppTheme.neonGreen,
            shadows: [
              Shadow(
                color: AppTheme.neonGreen.withValues(alpha: 0.5),
                blurRadius: 10,
              ),
            ],
          ),
        ),
        const SizedBox(height: 8),

        Text(
          'FINANÇAS GAMIFICADAS',
          style: AppTheme.pixelSmall.copyWith(
            color: AppTheme.lightPurple,
            letterSpacing: 2,
          ),
        ),
      ],
    );
  }

  Widget _buildLoginCard() {
    return Container(
      width: double.infinity,
      constraints: const BoxConstraints(maxWidth: 400),
      decoration: AppTheme.retroBorder,
      padding: const EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          // Título do card
          Text(
            '> LOGIN',
            style: AppTheme.pixelHeading.copyWith(color: AppTheme.neonGreen),
          ),
          const SizedBox(height: 24),

          // Campo Nickname
          TextFormField(
            controller: _nicknameController,
            style: AppTheme.pixelBody.copyWith(color: AppTheme.white),
            decoration: AppTheme.inputDecoration(
              'NICKNAME',
              icon: Icons.person,
            ),
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Digite seu nickname';
              }
              if (value.length < 3) {
                return 'Mínimo 3 caracteres';
              }
              return null;
            },
          ),
          const SizedBox(height: 16),

          // Campo Senha
          TextFormField(
            controller: _passwordController,
            obscureText: _obscurePassword,
            style: AppTheme.pixelBody.copyWith(color: AppTheme.white),
            decoration: AppTheme.inputDecoration('SENHA', icon: Icons.lock)
                .copyWith(
                  suffixIcon: IconButton(
                    icon: Icon(
                      _obscurePassword
                          ? Icons.visibility
                          : Icons.visibility_off,
                      color: AppTheme.accentPurple,
                    ),
                    onPressed: () =>
                        setState(() => _obscurePassword = !_obscurePassword),
                  ),
                ),
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Digite sua senha';
              }
              if (value.length < 4) {
                return 'Mínimo 4 caracteres';
              }
              return null;
            },
          ),
          const SizedBox(height: 24),

          // Mensagem de erro
          if (_errorMessage != null) ...[
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: AppTheme.error.withValues(alpha: 0.2),
                border: Border.all(color: AppTheme.error, width: 2),
              ),
              child: Row(
                children: [
                  Icon(Icons.error_outline, color: AppTheme.error, size: 20),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      _errorMessage!,
                      style: AppTheme.pixelSmall.copyWith(
                        color: AppTheme.error,
                      ),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
          ],

          // Botão de Login
          SizedBox(
            height: 56,
            child: ElevatedButton(
              onPressed: _isLoading ? null : _handleLogin,
              style: AppTheme.retroButtonPrimary,
              child: _isLoading
                  ? SizedBox(
                      width: 24,
                      height: 24,
                      child: CircularProgressIndicator(
                        strokeWidth: 3,
                        valueColor: AlwaysStoppedAnimation(AppTheme.white),
                      ),
                    )
                  : Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text('ENTRAR', style: AppTheme.pixelButton),
                        const SizedBox(width: 8),
                        Icon(Icons.arrow_forward, size: 20),
                      ],
                    ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildRegisterLink() {
    return TextButton(
      onPressed: _navigateToRegister,
      style: AppTheme.retroButtonSecondary,
      child: RichText(
        text: TextSpan(
          style: AppTheme.pixelSmall,
          children: [
            TextSpan(
              text: 'SEM CONTA? ',
              style: TextStyle(color: AppTheme.lightPurple),
            ),
            TextSpan(
              text: 'REGISTRE-SE',
              style: TextStyle(color: AppTheme.neonGreen),
            ),
          ],
        ),
      ),
    );
  }
}
